/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2003 Israel Olalla
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA 02111-1307, USA
 *
 *  For questions, suggestions:
 *
 *  iolalla@yahoo.com
 *
 */

package com.lambo.schedule.data;

import org.apache.log4j.Logger;

/***
 * This DAO Gives all the methods necesary to build CrontabEntries
 * This class is an abstraction to make esaier the integration of new
 * DataSources that help to access CrontabEntries in new ways
 * @author $Author: jiadx $
 * @version $Revision: 2875 $
 */
public class CrontabEntryDAO {
	static Logger Log = Logger.getLogger(CrontabEntryDAO.class.getName());
	/**
	 *This insntance grants only an instance of this DAO in
	 * every system 
	 */
	private static CrontabEntryDAO instance;

	/** This DataSource is the reason of this class */
	private static DataSource dao = null;

	/**
	 * Default constructor This one initializes everything maybe 
	 * could use lazy inizialization
	 */
	private CrontabEntryDAO() {
		if (dao == null) {
			dao = DataFactory.getInstance().getDAO();
		}
	}
	/**
	 *	This method returns the singleton is very important to grant
	 *  That only a Thread accesses at a time
	 */
	public synchronized static CrontabEntryDAO getInstance() {
		if (instance == null) {
			instance = new CrontabEntryDAO();
		}
		return instance;
	}
	public void loadAllCrontabEntry() throws Exception {
		dao.loadAllCrontabEntry();
	}
	/**
	 *	Gets all the CrontabEntryBean from the DataSource
	 * @return CrontabEntryBean[]
	 * @throws Exception
	 */
	public CrontabEntryBean[] findAll() throws Exception {
		return dao.findAll();
	}
	/**
	*	searches  the CrontabEntryBean from the DataSource
	* @return CrontabEntryBean
	* @throws Exception
	*/
	public CrontabEntryBean find(CrontabEntryBean ceb) throws Exception {
		return dao.find(ceb);
	}

	public CrontabEntryBean find(String name) throws Exception {
		return dao.findByName(name);
	}
	/**
	*	stores CrontabEntryBean in  the DataSource
	* @param CrontabEntryBean list
	* @throws Exception
	*/
	public void store(CrontabEntryBean[] list) throws Exception {
		dao.store(list);
	}
	/**
	* stores CrontabEntryBean in  the DataSource
	* @param CrontabEntryBean
	* @throws Exception
	*/
	public void store(CrontabEntryBean bean) throws Exception {
		dao.store(bean);
	}
	/**
	* removes CrontabEntryBean from the DataSource
	* @param CrontabEntryBean
	* @throws Exception
	*/
	public void remove(String name) throws Exception {
		dao.remove(name);
	}
	
	public String getDataSourceType(){
		return dao.getType();
	}

	/**
	* removes CrontabEntryBean from the DataSource
	* @param CrontabEntryBean
	* @throws Exception
	*/
	//public void remove(CrontabEntryBean[] list) throws Exception {
	//	dao.remove(list);
	//}
	
}
