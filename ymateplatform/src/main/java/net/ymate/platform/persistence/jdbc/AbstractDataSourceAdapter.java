/*
 * Copyright 2007-2107 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ymate.platform.persistence.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.persistence.jdbc.base.dialect.IDialect;
import net.ymate.platform.persistence.jdbc.support.JdbcDataSourceCfgMeta;

/**
 * <p>
 * AbstractDataSourceAdapter
 * </p>
 * <p>
 * 数据源适配器接口抽象实现类；
 * </p>
 * 
 * @author 刘镇(suninformation@163.com)
 * @version 0.0.0
 *          <table style="border:1px solid gray;">
 *          <tr>
 *          <th width="100px">版本号</th><th width="100px">动作</th><th
 *          width="100px">修改人</th><th width="100px">修改时间</th>
 *          </tr>
 *          <!-- 以 Table 方式书写修改历史 -->
 *          <tr>
 *          <td>0.0.0</td>
 *          <td>创建类</td>
 *          <td>刘镇</td>
 *          <td>2013年8月1日下午8:30:34</td>
 *          </tr>
 *          </table>
 */
public abstract class AbstractDataSourceAdapter implements IDataSourceAdapter {

	protected JdbcDataSourceCfgMeta cfgMeta;

	protected IDialect dialect;

	/* (non-Javadoc)
	 * @see net.ymate.platform.persistence.jdbc.IDataSourceAdapter#initialize(net.ymate.platform.persistence.jdbc.support.DataSourceCfgMeta)
	 */
	public void initialize(JdbcDataSourceCfgMeta cfgMeta) {
		this.cfgMeta = cfgMeta;
	}

	/* (non-Javadoc)
	 * @see net.ymate.platform.persistence.jdbc.IDataSourceAdapter#getDialect()
	 */
	public IDialect getDialect() {
		if (dialect == null) {
			Connection _conn = null;
			try {
				_conn = this.getConnection();
				String _prodName = _conn.getMetaData().getDatabaseProductName();
				dialect = JDBC.getDialectClass(_prodName).newInstance();
			} catch (Exception e) {
				throw new Error(RuntimeUtils.unwrapThrow(e));
			} finally {
				if (_conn != null) {
					try {
						_conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					_conn = null;
				}
			}
		}
		return dialect;
	}

	/* (non-Javadoc)
	 * @see net.ymate.platform.persistence.jdbc.IDataSourceAdapter#destroy()
	 */
	public void destroy() {
		this.cfgMeta = null;
		this.dialect = null;
	}

}
