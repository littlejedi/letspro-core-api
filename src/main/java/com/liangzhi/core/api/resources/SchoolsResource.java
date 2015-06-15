package com.liangzhi.core.api.resources;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangzhi.commons.domain.School;
import com.liangzhi.commons.domain.schools.CountryRegion;
import com.liangzhi.commons.domain.schools.Region;
import com.liangzhi.commons.domain.schools.SchoolList;

@Path("/schools")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Component
public class SchoolsResource {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Path("/middleschools/map")
	@GET
	public String getMiddleSchoolsMapping() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream("assets/middleschools.txt");
		byte[] bytes = IOUtils.toByteArray(is);
		return new String(bytes);
	}
	
	@Path("/highschools/map")
	@GET
	public String getHighSchoolsMapping() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream("assets/highschools.txt");
		byte[] bytes = IOUtils.toByteArray(is);
		return new String(bytes);
	}
	
	@Path("/middleschools/list")
	@GET
	public Map<Integer, School> getMiddleSchoolsList() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream("assets/middleschools.txt");
		byte[] bytes = IOUtils.toByteArray(is);
		Map<Integer, School> map = new HashMap<Integer, School>();
		SchoolList list = MAPPER.readValue(bytes, SchoolList.class);
		List<CountryRegion> countries = list.getCountries();
		for (CountryRegion country : countries) {
			for (Region r : country.getSubregions()) {
				addSchoolsToMap(r, map);
			}
		}
		return map;
	}
	
	@Path("/highschools/list")
	@GET
	public Map<Integer, School> getHighSchoolsList() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream("assets/highschools.txt");
		byte[] bytes = IOUtils.toByteArray(is);
		Map<Integer, School> map = new HashMap<Integer, School>();
		SchoolList list = MAPPER.readValue(bytes, SchoolList.class);
		List<CountryRegion> countries = list.getCountries();
		for (CountryRegion country : countries) {
			for (Region r : country.getSubregions()) {
				addSchoolsToMap(r, map);
			}
		}
		return map;
	}
	
	private void addSchoolsToMap(Region region, Map<Integer, School> map) {
		if (region.getSchools() != null && region.getSchools().size() > 0) {
			for (School s : region.getSchools()) {
				map.put(s.getId(), s);
			}
			return;
		}
		for (Region r : region.getSubregions()) {
			addSchoolsToMap(r, map);
		}
	}
}
