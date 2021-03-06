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
package net.ymate.platform.commons.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * <p>
 * II18NEventHandler
 * </p>
 * <p>
 * 国际化资源管理器事件监听处理器；
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
 *          <td>2013-4-14下午2:44:08</td>
 *          </tr>
 *          </table>
 */
public interface II18NEventHandler {

	/**
	 * @return 加载当前Locale
	 */
	public Locale loadCurrentLocale();

	/**
	 * @param locale 当Locale改变时处理此方法
	 */
	public void onLocaleChanged(Locale locale);

	/**
	 * @param resourceName 资源名称
	 * @return 加载资源文件的具体处理方法
	 * @throws IOException
	 */
	public InputStream onLoadProperties(String resourceName) throws IOException;

}
