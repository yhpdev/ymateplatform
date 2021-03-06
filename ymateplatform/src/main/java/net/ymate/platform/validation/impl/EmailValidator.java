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
package net.ymate.platform.validation.impl;

import net.ymate.platform.validation.IValidateContext;

/**
 * <p>
 * EmailValidator
 * </p>
 * <p>
 * 邮箱地址格式验证；
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
 *          <td>2013-4-17下午2:03:56</td>
 *          </tr>
 *          </table>
 */
public class EmailValidator extends RegexValidator {

	public static final String NAME = "email";

	/* (non-Javadoc)
	 * @see net.ymate.platform.validation.impl.RegexValidator#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/* (non-Javadoc)
	 * @see net.ymate.platform.validation.AbstractValidator#doParamsLengthCheck(net.ymate.platform.validation.IValidateContext, int)
	 */
	@Override
	protected boolean doParamsLengthCheck(IValidateContext context, int minLength) {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.ymate.platform.validation.AbstractValidator#getSingleParam(net.ymate.platform.validation.IValidateContext)
	 */
	@Override
	protected String getSingleParam(IValidateContext context) {
		return "(?:\\w[-._\\w]*\\w@\\w[-._\\w]*\\w\\.\\w{2,3}$)";
	}

	/* (non-Javadoc)
	 * @see net.ymate.platform.validation.impl.RegexValidator#getResultMessageI18nStr(net.ymate.platform.validation.IValidateContext)
	 */
	@Override
	protected String getResultMessageI18nStr(IValidateContext context) {
		return "ymp.validation.regex_email";
	}

}
