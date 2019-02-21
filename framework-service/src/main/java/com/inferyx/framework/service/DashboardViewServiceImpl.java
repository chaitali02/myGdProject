/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IDashboardDao;
import com.inferyx.framework.dao.IVizpodDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Dashboard;
import com.inferyx.framework.domain.DashboardExec;
import com.inferyx.framework.domain.DashboardExecView;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Section;
import com.inferyx.framework.domain.SectionView;
import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.domain.Vizpod;
import com.inferyx.framework.view.metadata.DashboardView;

@Service
public class DashboardViewServiceImpl {

	static final Logger logger = Logger.getLogger(DashboardViewServiceImpl.class);
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	IDashboardDao idashboardDao;
	@Autowired
	IVizpodDao iVizpodDao;
	@Autowired
	DashboardServiceImpl dashboardServiceImpl;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	VizpodServiceImpl vizpodServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;

	public Dashboard save(DashboardView dashboardView) throws Exception {
		Dashboard dashboard = new Dashboard();
		if (dashboardView.getUuid() != null) {
			dashboard.setUuid(dashboardView.getUuid());
		}

		MetaIdentifierHolder relationHolder = dashboardView.getDependsOn();
		dashboard.setTags(dashboardView.getTags());
		dashboard.setDesc(dashboardView.getDesc());
		dashboard.setFilterInfo(dashboardView.getFilterInfo());
		dashboard.setName(dashboardView.getName());
		dashboard.setDependsOn(relationHolder);
		List<SectionView> sectionViewInfo = dashboardView.getSectionInfo();
		List<Section> sectionInfo = new ArrayList<>();
		for (SectionView sectionView : sectionViewInfo) {
			Section section = new Section();
			section.setColNo(sectionView.getColNo());
			section.setRowNo(sectionView.getRowNo());
			section.setName(sectionView.getName());
			section.setSectionId(sectionView.getSectionId());
			Vizpod vizpod = sectionView.getVizpodInfo();
			if (vizpod != null) {
				MetaIdentifierHolder vizpodHolder = new MetaIdentifierHolder(
						new MetaIdentifier(MetaType.vizpod, vizpod.getUuid(), vizpod.getVersion()));
				section.setVizpodInfo(vizpodHolder);
			}
			sectionInfo.add(section);
		}
		dashboard.setSectionInfo(sectionInfo);
		dashboard.setPublished(dashboardView.getPublished());
		Dashboard dashboardDet = dashboardServiceImpl.save(dashboard);
		return dashboardDet;

	}

	public DashboardView findLatestByUuid(String uuid)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, ParseException, JsonProcessingException {
		DashboardView dashboardView = new DashboardView();
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;*/
		//Dashboard dashboard = idashboardDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		Dashboard dashboard = (Dashboard) commonServiceImpl.getLatestByUuid(uuid, MetaType.dashboard.toString());
		Dashboard resolvedDashboard = (Dashboard) commonServiceImpl.resolveName(dashboard, MetaType.dashboard);
		dashboardView.setId(resolvedDashboard.getId());
		dashboardView.setUuid(resolvedDashboard.getUuid());
		dashboardView.setVersion(resolvedDashboard.getVersion());
		dashboardView.setName(resolvedDashboard.getName());
		dashboardView.setDesc(resolvedDashboard.getDesc());
		dashboardView.setAppInfo(resolvedDashboard.getAppInfo());
		dashboardView.setCreatedBy(resolvedDashboard.getCreatedBy());
		dashboardView.setTags(resolvedDashboard.getTags());
		dashboardView.setActive(resolvedDashboard.getActive());
		dashboardView.setLocked(resolvedDashboard.getLocked());
		dashboardView.setCreatedOn(resolvedDashboard.getCreatedOn());

		List<Section> sectionInfo = resolvedDashboard.getSectionInfo();
		List<SectionView> sectionViewInfo = new ArrayList<>();
		for (Section section : sectionInfo) {
			SectionView sectionView = new SectionView();
			sectionView.setSectionId(section.getSectionId());
			sectionView.setName(section.getName());
			sectionView.setRowNo(section.getRowNo());
			sectionView.setColNo(section.getColNo());
			MetaIdentifierHolder vizpodInfo = section.getVizpodInfo();
			Vizpod vizpod = null;
			if (StringUtils.isBlank(vizpodInfo.getRef().getVersion())) {
				//vizpod = iVizpodDao.findLatestByUuid(vizpodInfo.getRef().getUuid(), new Sort(Sort.Direction.DESC, "version"));
				vizpod = (Vizpod) commonServiceImpl.getLatestByUuid(vizpodInfo.getRef().getUuid(), MetaType.vizpod.toString());
			} else {
				//vizpod = iVizpodDao.findOneByUuidAndVersion(vizpodInfo.getRef().getUuid(), vizpodInfo.getRef().getVersion());
				vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodInfo.getRef().getUuid(), vizpodInfo.getRef().getVersion(), MetaType.vizpod.toString());
			}
			vizpod = vizpodServiceImpl.resolveName(vizpod);
			sectionView.setVizpodInfo(vizpod);
			sectionViewInfo.add(sectionView);
		}
		dashboardView.setSectionInfo(sectionViewInfo);

		MetaIdentifierHolder dependsOn = resolvedDashboard.getDependsOn();

		// List<MetaIdentifierHolder> filterInfo = resolvedDashboard.getFilterInfo();

		if (dependsOn.getRef().getType().equals(MetaType.datapod)) {
			// Datapod datapod =
			// datapodServiceImpl.findLatestByUuid(dependsOn.getRef().getUuid());
			Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(dependsOn.getRef().getUuid(), MetaType.datapod.toString());
			dependsOn.getRef().setName(datapod.getName());
		} 
		else if (dependsOn.getRef().getType().equals(MetaType.dataset)) {
			// Datapod datapod =
			// datapodServiceImpl.findLatestByUuid(dependsOn.getRef().getUuid());
			DataSet dataSet = (DataSet) commonServiceImpl.getLatestByUuid(dependsOn.getRef().getUuid(), MetaType.dataset.toString());
			dependsOn.getRef().setName(dataSet.getName());
		} 
		else {
			// Relation
			// relation=relationServiceImpl.findLatestByUuid(dependsOn.getRef().getUuid());
			Relation relation = (Relation) commonServiceImpl.getLatestByUuid(dependsOn.getRef().getUuid(), MetaType.relation.toString());
			dependsOn.getRef().setName(relation.getName());
		}
		/* Filter resolvedFilter = null; */
		/*
		 * if(filterInfo != null) { for (int i = 0; i < filterInfo.size(); i++) { Filter
		 * filter = filterServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(),
		 * dashboard.getVersion()); resolvedFilter =
		 * filterServiceImpl.resolveName(filter); }
		 */
		// }

		// dashboardView.setFilter(resolvedFilter);
		// Relation relation = relationServiceImpl.getAsOf(dependsOn.getRef().getUuid(),
		// dataset.getVersion());
		// Relation resolvedRelation = relationServiceImpl.resolveName(relation);
		/*
		 * System.out.println("\n\n"); for(AttributeRefHolder holder :
		 * resolvedDashboard.getFilterInfo()){
		 * System.out.println(holder.toString()+holder.getRef().toString()+"\n\n"); }
		 */
		List<AttributeRefHolder> filterInfo = new ArrayList<>();

		for (int i = 0; i < resolvedDashboard.getFilterInfo().size(); i++) {
			MetaIdentifier meta = new MetaIdentifier();
			AttributeRefHolder attrHolder = new AttributeRefHolder();

			meta.setUuid(resolvedDashboard.getFilterInfo().get(i).getRef().getUuid());
			meta.setVersion(resolvedDashboard.getFilterInfo().get(i).getRef().getVersion());
			meta.setType(resolvedDashboard.getFilterInfo().get(i).getRef().getType());
			meta.setName(resolvedDashboard.getFilterInfo().get(i).getRef().getName());

			attrHolder.setAttrId(resolvedDashboard.getFilterInfo().get(i).getAttrId());
			attrHolder.setAttrName(resolvedDashboard.getFilterInfo().get(i).getAttrName());
			attrHolder.setRef(meta);
			// System.out.println(meta.toString());
			filterInfo.add(attrHolder);
		}
		dashboardView.setFilterInfo(filterInfo);
		dashboardView.setDependsOn(dependsOn);
		return dashboardView;
	}

	public List<DashboardView> getDashboardViews(List<Dashboard> dashboardList)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, ParseException, JsonProcessingException {
		if (dashboardList == null) {
			return null;
		}
		List<DashboardView> dashboardViewList = new ArrayList<>();
		DashboardView dashboardView = null;

		for (Dashboard dashboard : dashboardList) {
			Dashboard resolvedDashboard = (Dashboard) commonServiceImpl.resolveName(dashboard, MetaType.dashboard);
			dashboardView = new DashboardView();
			dashboardView.setUuid(resolvedDashboard.getUuid());
			dashboardView.setVersion(resolvedDashboard.getVersion());
			dashboardView.setName(resolvedDashboard.getName());
			dashboardView.setDesc(resolvedDashboard.getDesc());
			dashboardView.setAppInfo(resolvedDashboard.getAppInfo());
			dashboardView.setCreatedBy(resolvedDashboard.getCreatedBy());
			dashboardView.setTags(resolvedDashboard.getTags());
			dashboardView.setActive(resolvedDashboard.getActive());
			dashboardView.setCreatedOn(resolvedDashboard.getCreatedOn());
			List<Section> sectionInfo = resolvedDashboard.getSectionInfo();
			List<SectionView> sectionViewInfo = new ArrayList<>();
			for (Section section : sectionInfo) {
				SectionView sectionView = new SectionView();
				sectionView.setSectionId(section.getSectionId());
				sectionView.setName(section.getName());
				sectionView.setRowNo(section.getRowNo());
				sectionView.setColNo(section.getColNo());
				MetaIdentifierHolder vizpodInfo = section.getVizpodInfo();
				Vizpod vizpod = null;
				if (StringUtils.isBlank(vizpodInfo.getRef().getVersion())) {
					//vizpod = iVizpodDao.findLatestByUuid(vizpodInfo.getRef().getUuid(), new Sort(Sort.Direction.DESC, "version"));
					vizpod = (Vizpod) commonServiceImpl.getLatestByUuid(vizpodInfo.getRef().getUuid(), MetaType.vizpod.toString());
				} else {
					//vizpod = iVizpodDao.findOneByUuidAndVersion(vizpodInfo.getRef().getUuid(), vizpodInfo.getRef().getVersion());
					vizpod =  (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodInfo.getRef().getUuid(), vizpodInfo.getRef().getVersion(), MetaType.vizpod.toString());
				}
				sectionView.setVizpodInfo(vizpod);
				sectionViewInfo.add(sectionView);
			}
			dashboardView.setSectionInfo(sectionViewInfo);
			MetaIdentifierHolder dependsOn = resolvedDashboard.getDependsOn();

			// List<MetaIdentifierHolder> filterInfo = resolvedDashboard.getFilterInfo();

			if (dependsOn.getRef().getType().equals(MetaType.datapod)) {
				// datapodServiceImpl.findLatestByUuid(dependsOn.getRef().getUuid());
				Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(dependsOn.getRef().getUuid(),	MetaType.datapod.toString());
				dependsOn.getRef().setName(datapod.getName());
			} else {
				// relation=relationServiceImpl.findLatestByUuid(dependsOn.getRef().getUuid());
				Relation relation = (Relation) commonServiceImpl.getLatestByUuid(dependsOn.getRef().getUuid(), MetaType.relation.toString());
				dependsOn.getRef().setName(relation.getName());
			}

			/*
			 * Filter resolvedFilter = null;
			 * 
			 * dashboardView.setFilter(resolvedFilter);
			 */
			dashboardView.setFilterInfo(resolvedDashboard.getFilterInfo());
			dashboardView.setDependsOn(dependsOn);
			dashboardViewList.add(dashboardView);
		}
		return dashboardViewList;
	}

	public DashboardView findOneByUuidAndVersion(String uuid, String version)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, ParseException, JsonProcessingException {
		DashboardView dashboardView = new DashboardView();
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;		*/
		/*if (appUuid == null) {
			dashboard = idashboardDao.findOneByUuidAndVersion(uuid, version);
		} else {
			dashboard = idashboardDao.findOneByUuidAndVersion(appUuid, uuid, version);
		}*/
		Dashboard dashboard = (Dashboard) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dashboard.toString());
//		Dashboard resolvedDashboard = (Dashboard) commonServiceImpl.resolveName(dashboard, MetaType.dashboard);
		dashboardView.setUuid(dashboard.getUuid());
		dashboardView.setVersion(dashboard.getVersion());
		dashboardView.setName(dashboard.getName());
		dashboardView.setDesc(dashboard.getDesc());
		dashboardView.setAppInfo(dashboard.getAppInfo());
		dashboardView.setCreatedBy(dashboard.getCreatedBy());
		dashboardView.setTags(dashboard.getTags());
		dashboardView.setActive(dashboard.getActive());
		dashboardView.setCreatedOn(dashboard.getCreatedOn());

		List<Section> sectionInfo = dashboard.getSectionInfo();
		List<SectionView> sectionViewInfo = new ArrayList<>();
		for (Section section : sectionInfo) {
			SectionView sectionView = new SectionView();
			sectionView.setSectionId(section.getSectionId());
			sectionView.setName(section.getName());
			sectionView.setRowNo(section.getRowNo());
			sectionView.setColNo(section.getColNo());
			MetaIdentifierHolder vizpodInfo = section.getVizpodInfo();
			Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodInfo.getRef().getUuid(), vizpodInfo.getRef().getVersion(), MetaType.vizpod.toString());
//			vizpod = vizpodServiceImpl.resolveName(vizpod);
			sectionView.setVizpodInfo(vizpod);
			sectionViewInfo.add(sectionView);
		}
		dashboardView.setSectionInfo(sectionViewInfo);

		MetaIdentifierHolder dependsOn = dashboard.getDependsOn();

		// List<MetaIdentifierHolder> filterInfo = resolvedDashboard.getFilterInfo();

		if (dependsOn.getRef().getType().equals(MetaType.datapod)) {
			Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(dependsOn.getRef().getUuid(),	MetaType.datapod.toString());
			dependsOn.getRef().setName(datapod.getName());
		} else {
			// relation=relationServiceImpl.findLatestByUuid(dependsOn.getRef().getUuid());
			Relation relation = (Relation) commonServiceImpl.getLatestByUuid(dependsOn.getRef().getUuid(), MetaType.relation.toString());
			dependsOn.getRef().setName(relation.getName());
		}

		// Filter resolvedFilter = null;
		/*
		 * if(filterInfo != null) { for (int i = 0; i < filterInfo.size(); i++) { Filter
		 * filter = filterServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(),
		 * dashboard.getVersion()); resolvedFilter =
		 * filterServiceImpl.resolveName(filter); } }
		 */

		// dashboardView.setFilter(resolvedFilter);
		dashboardView.setFilterInfo(dashboard.getFilterInfo());
		dashboardView.setDependsOn(dependsOn);
		return dashboardView;
	}

	/**
	 * @param uuid
	 * @param version
	 * @return DashboardExecView
	 * @throws JsonProcessingException 
	 */
	public DashboardExecView findOneExecByUuidAndVersion(String execUuid, String execVersion) throws JsonProcessingException {
		DashboardExec dashboardExec = (DashboardExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.dashboardExec.toString(), "N");
		MetaIdentifier dependsOnMI = dashboardExec.getDependsOn().getRef();
		Dashboard dashboard = (Dashboard) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
		
		DashboardExecView dashboardExecView = new DashboardExecView();

		//setting base entity
		dashboardExecView.setUuid(dashboardExec.getUuid());
		dashboardExecView.setVersion(dashboardExec.getVersion());
		dashboardExecView.setName(dashboardExec.getName());
		dashboardExecView.setDesc(dashboardExec.getDesc());
		dashboardExecView.setCreatedBy(dashboardExec.getCreatedBy());
		dashboardExecView.setCreatedOn(dashboardExec.getCreatedOn());
		dashboardExecView.setTags(dashboardExec.getTags());
		dashboardExecView.setActive(dashboardExec.getActive());
		dashboardExecView.setLocked(dashboardExec.getLocked());
		dashboardExecView.setPublished(dashboardExec.getPublished());
		dashboardExecView.setAppInfo(dashboardExec.getAppInfo());
		dashboardExecView.setPublicFlag(dashboardExec.getPublicFlag());
		
		//setting specific properties
		List<SectionView> sectionViewInfo = new ArrayList<>();
		for(Section section : dashboard.getSectionInfo()) {
			MetaIdentifier vizpodMI = section.getVizpodInfo().getRef();
			for(MetaIdentifierHolder vizExecHolder : dashboardExec.getVizExecInfo()) {
				MetaIdentifier vizExecMI = vizExecHolder.getRef();
				VizExec vizExec = (VizExec) commonServiceImpl.getOneByUuidAndVersion(vizExecMI.getUuid(), vizExecMI.getVersion(), vizExecMI.getType().toString(), "N");
				MetaIdentifier vizExecDependsOnMI = vizExec.getDependsOn().getRef();
				Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizExecDependsOnMI.getUuid(), vizExecDependsOnMI.getVersion(), vizExecDependsOnMI.getType().toString(), "Y");
				if(vizpod.getUuid().equalsIgnoreCase(vizpodMI.getUuid())) {
					try {					
						SectionView sectionView = new SectionView();
						sectionView.setVizpodInfo(vizpod);
						sectionView.setVizExecInfo(vizExec);
						sectionView.setColNo(section.getColNo());
						sectionView.setName(section.getName());
						sectionView.setRowNo(section.getRowNo());
						sectionView.setSectionId(section.getSectionId());
						sectionViewInfo.add(sectionView);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		dashboardExecView.setSectionViewInfo(sectionViewInfo);
		dashboardExecView.setDependsOn(dashboardExec.getDependsOn());
		dashboardExecView.setDashboard(dashboard);
		dashboardExecView.setFilterInfo(dashboard.getFilterInfo());
		return dashboardExecView;
	}

}
