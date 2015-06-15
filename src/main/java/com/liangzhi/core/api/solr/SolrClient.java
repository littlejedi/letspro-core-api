package com.liangzhi.core.api.solr;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.liangzhi.commons.domain.Project;
import com.liangzhi.commons.domain.ProjectAuthor;
import com.liangzhi.commons.domain.ProjectView;
import com.liangzhi.commons.domain.SolrProjectDocument;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Component
public class SolrClient {
	
	private WebResource webResource;
	private Client client;
	
	//private static final String SOLR_SERVER_POST_JSON_URL = "http://www.stemcloud.cn:8983/solr/update/json?commit=true";
	private static final String SOLR_SERVER_BASE_URL = "http://www.stemcloud.cn:8983/solr";
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrClient.class);
	
	private SolrServer solrServer = null;
	
	@PostConstruct
	public void init() throws Exception {
		client = Client.create();
		webResource = client.resource(SOLR_SERVER_BASE_URL);
		/*if (solrServer == null) {
			solrServer = new CommonsHttpSolrServer(SOLR_SERVER_BASE_URL);
		}*/
	}
	
	public List<SolrProjectDocument> searchProjects(String q) {
		List<SolrProjectDocument> result = Lists.newArrayList();
		try {
	        ClientResponse response = this.query(q);
	        String responseBody = response.getEntity(String.class);
	        JsonNode rootNode = MAPPER.readValue(responseBody, JsonNode.class);
	        if (response.getStatus() >= 400) {
	        	processErrorResponse(q, rootNode);
	        	return result;
	        }
	        String qtime = rootNode.path("responseHeader").path("QTime").asText();
	        String numFound = rootNode.path("response").path("numFound").asText();
	        for (JsonNode doc : rootNode.path("response").path("docs")) {
	        	SolrProjectDocument solrDoc = MAPPER.readValue(doc.toString(), SolrProjectDocument.class);
	        	result.add(solrDoc);
	        }
	        LOGGER.info("Query={}, qtime={}, numFound={}", q, qtime, numFound);
	        return result;
	        
        } catch (Exception e) {
	        LOGGER.error("An error occured executing query=" + q, e);
	        return result;
        }
	}
	
	public ClientResponse addProjectViewPayload(ProjectView payload) throws JsonProcessingException {
		final Project project = payload.getProject();
		List<ProjectAuthor> authors = payload.getAuthors();
		SolrProjectDocument doc = new SolrProjectDocument();
		doc.setId(project.getId());
		doc.setAbstractText(project.getAbstractText());
		doc.setProjectName(project.getName());
		doc.setParticipatedFairId(project.getParticipatedFairId());
		doc.setCategoryId(project.getCategoryId());
		
		List<String> sas = Lists.newArrayList();
		if (authors != null) {
			for (ProjectAuthor a : authors) {
				sas.add(a.getName());
			}
		}
		doc.setAuthors(sas);
	
		List<SolrProjectDocument> result = Lists.newArrayList();
		result.add(doc);	
		return post(result);
	}
	
	private ClientResponse post(Object obj) throws JsonProcessingException {
		String json = MAPPER.writeValueAsString(obj);
		return webResource.path("update").path("json").queryParam("commit", "true").type(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);
	}
	
	private ClientResponse query(String query) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("q", query);
		params.add("wt", "json");
		return webResource.path("select").queryParams(params).get(ClientResponse.class);
	}
	
	private void processErrorResponse(String q, JsonNode rootNode) {
	    String errorMsg = rootNode.path("error").path("msg").asText();
	    String errorCode = rootNode.path("error").path("code").asText();
	    LOGGER.error("Solr server returned error response. Error code={}, error message={} for query=" + q, errorCode, errorMsg);
    }
}
