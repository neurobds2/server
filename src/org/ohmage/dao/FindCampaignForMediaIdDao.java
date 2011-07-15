/*******************************************************************************
 * Copyright 2011 The Regents of the University of California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ohmage.dao;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.ohmage.request.AwRequest;
import org.ohmage.request.MediaQueryAwRequest;
import org.springframework.jdbc.core.SingleColumnRowMapper;


/**
 * @author selsky
 */
public class FindCampaignForMediaIdDao extends AbstractDao {
	private static Logger _logger = Logger.getLogger(FindCampaignForMediaIdDao.class);
	
	private String _sql = "SELECT c.urn"
		  	             + " FROM campaign c, survey_response sr, prompt_response pr, user u"
		  	             + " WHERE pr.response = ?"
		  	             + " AND pr.survey_response_id = sr.id" 
		  	             + " AND sr.campaign_id = c.id"
		  	             + " AND sr.user_id = u.id"
		  	             + " AND u.username = ?";
	
	public FindCampaignForMediaIdDao(DataSource dataSource) {
		super(dataSource);
	}
	
	/**
	 * Finds the campaign URN for the media id and user in the request.
	 */
	@Override
	public void execute(AwRequest awRequest) {
		try { 
			// FIXME -- this should be returning a single row so all that's needed is a query() that returns an Object
			awRequest.setResultList(
				getJdbcTemplate().query(
					_sql, 
					new Object[] { ((MediaQueryAwRequest) awRequest).getMediaId(), ((MediaQueryAwRequest) awRequest).getUserNameRequestParam() },
					new SingleColumnRowMapper())
			);
		}	
		catch (org.springframework.dao.DataAccessException dae) {
			_logger.error("a DataAccessException occurred when running the following sql '" + _sql + "' with the parameter "
				+ ((MediaQueryAwRequest) awRequest).getMediaId() , dae);
			throw new DataAccessException(dae);
		}
	}
}