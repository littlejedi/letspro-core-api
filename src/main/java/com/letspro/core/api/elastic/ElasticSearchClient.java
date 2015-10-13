package com.letspro.core.api.elastic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letspro.commons.domain.SensorDataRecord;
import com.letspro.commons.domain.elastic.SensorDataDocument;
import com.letspro.commons.utils.SensorDataUtils;

public class ElasticSearchClient {
    
    private static final int SCROLL_SIZE = 1000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchClient.class);
    
    private Client client;
    
    private ObjectMapper MAPPER = new ObjectMapper();

    public ElasticSearchClient(ElasticSearchConfiguration configuration) {
        String host = configuration.getHost();
        int port = configuration.getPort();
        // on startup
        this.client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress(host, port));
    }
    
    public void indexSensorDataDocument(SensorDataDocument doc) throws Exception {
        String json = MAPPER.writeValueAsString(doc);
        IndexResponse response = client
                .prepareIndex("stem", "sensordata")
                .setSource(json).execute().actionGet();
        boolean created = response.isCreated();
    }
    
    public void indexMultipleSensorDataDocuments(List<SensorDataDocument> docs) throws Exception {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (SensorDataDocument doc : docs) {
            String json = MAPPER.writeValueAsString(doc);
            bulkRequest.add(client.prepareIndex("stem", "sensordata")
                    .setSource(json));
        }
        BulkResponse response = bulkRequest.execute().actionGet();
        if (response.hasFailures()) {
            Iterator<BulkItemResponse> iter = response.iterator();
            while (iter.hasNext()) {
                BulkItemResponse r = iter.next();
                LOGGER.warn("Error occured inserting sensor data record in a bulk. Error=" + r.getFailureMessage());
            }
        }
    }
    
    public void queryDataBySensorId(String sensorId) throws Exception {
        //QueryBuilder qb = QueryBuilders.matchQuery("sensorId", sensorId);
        SearchResponse response = client.prepareSearch("stem")
                .setTypes("sensordata")
                .addSort("sensorDataTimestamp", SortOrder.ASC)
                .setQuery(QueryBuilders.matchQuery("sensorId", sensorId))
                .setFrom(0)
                .setSize(10000)
                //.setFrom(0).setSize(50).setExplain(true)
                .execute()
                .actionGet();
        SearchHits hits = response.getHits();
        int i = 1;
        if (hits.getTotalHits() > 0) {
            for (SearchHit h : hits) {
                //h.getInnerHits()
                System.out.println(h.getSourceAsString());
                SensorDataDocument doc = MAPPER.readValue(h.getSourceAsString(), SensorDataDocument.class);
                DateTime dt = new DateTime(doc.getTimestamp(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("PST")));
                System.out.println(dt.toString());
                i++;
            }
        }
    }
    
    public List<SensorDataRecord> queryData(String sensorId, String experimentId, Long start, Long end) throws Exception {
        List<SensorDataRecord> result = new ArrayList<SensorDataRecord>();
        SearchResponse response = null;
        int i = 0;
        FilterBuilder sensorIdFilter = sensorId != null ? FilterBuilders.termFilter("sensorId", sensorId) : FilterBuilders.matchAllFilter();
        FilterBuilder experimentIdFilter = experimentId != null ? FilterBuilders.termFilter("experimentId", experimentId) : FilterBuilders.matchAllFilter();
        while (response == null || response.getHits().hits().length != 0) {
            response = client.prepareSearch("stem")
                    .setTypes("sensordata")
                    .addSort("timestamp", SortOrder.ASC)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.filteredQuery(QueryBuilders.rangeQuery("timestamp").from(start).to(end), FilterBuilders.andFilter(sensorIdFilter, experimentIdFilter)))
                    .setFrom(i * SCROLL_SIZE)
                    .setSize(SCROLL_SIZE)
                    .execute()
                    .actionGet();
            SearchHits hits = response.getHits();
            if (hits.getTotalHits() > 0) {
                for (SearchHit h : hits) {
                    SensorDataDocument doc = MAPPER.readValue(h.getSourceAsString(), SensorDataDocument.class);
                    SensorDataRecord record = SensorDataUtils.toSensorDataRecord(doc);
                    result.add(record);
                }
            }
            i++;
        }
        return result;
    }

}
