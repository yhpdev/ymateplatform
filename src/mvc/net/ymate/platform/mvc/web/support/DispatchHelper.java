/**
 * <p>文件名:	DispatchHelper.java</p>
 * <p>版权:		详见产品版权说明书</p>
 * <p>公司:		YMateSoft Co., Ltd.</p>
 * <p>项目名：	ymateplatform</p>
 * <p>作者：		刘镇(suninformation@163.com)</p>
 */
package net.ymate.platform.mvc.web.support;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.module.base.YMP;
import net.ymate.platform.mvc.support.RequestExecutor;
import net.ymate.platform.mvc.view.IView;
import net.ymate.platform.mvc.web.IWebErrorHandler;
import net.ymate.platform.mvc.web.WebMVC;
import net.ymate.platform.mvc.web.context.IWebRequestContext;
import net.ymate.platform.mvc.web.context.WebContext;
import net.ymate.platform.mvc.web.context.impl.WebRequestContext;
import net.ymate.platform.mvc.web.view.impl.FreeMarkerView;
import net.ymate.platform.mvc.web.view.impl.HttpStatusView;
import net.ymate.platform.mvc.web.view.impl.JspView;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * DispatchHelper
 * </p>
 * <p>
 * 请求分发调度助手类；
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
 *          <td>2013年8月18日下午7:11:29</td>
 *          </tr>
 *          </table>
 */
public class DispatchHelper {

	private static final Log _LOG = LogFactory.getLog(DispatchHelper.class);

	public static final String DEFAULT_METHOD_PARAM = "_method";

	private String methodParam;

	/**
	 * 请求前缀
	 */
	private String prefix;

	private String baseViewFilePath;

	/**
	 * 构造器
	 * 
	 * @param config
	 */
	public DispatchHelper(FilterConfig config) {
		prefix = StringUtils.defaultIfEmpty(config.getInitParameter("prefix"), "");
        methodParam = StringUtils.defaultIfEmpty(config.getInitParameter("methodParam"), DEFAULT_METHOD_PARAM);
        baseViewFilePath = RuntimeUtils.getRootPath() + StringUtils.substringAfter(TemplateHelper.getRootViewPath(), "/WEB-INF/");
	}

	/**
	 * 构造器
	 * 
	 * @param config
	 */
	public DispatchHelper(ServletConfig config) {
		prefix = StringUtils.defaultIfEmpty(config.getInitParameter("prefix"), "");
        methodParam = StringUtils.defaultIfEmpty(config.getInitParameter("methodParam"), DEFAULT_METHOD_PARAM);
        baseViewFilePath = RuntimeUtils.getRootPath() + StringUtils.substringAfter(TemplateHelper.getRootViewPath(), "/WEB-INF/");
	}

	public IWebRequestContext bindRequestContext(HttpServletRequest request) {
		return new WebRequestContext(request, getPrefix());
	}

	public void doRequestProcess(IWebRequestContext context, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		StopWatch _stopWatch = new StopWatch();
		_stopWatch.start();
		try {
			_LOG.info("接收请求" + context.toString());
			if (YMP.IS_DEV_MODEL) {
				Enumeration<?> _headerNames = request.getHeaderNames();
				while(_headerNames.hasMoreElements()) {
					String _headerName = (String) _headerNames.nextElement();
					_LOG.info("请求头[" + _headerName + "]=" + request.getHeader(_headerName));
				}
			}
			request = wrapperRequestForREST(request);
			WebContext.setContext(new WebContext(WebContext.createWebContextMap(servletContext, request, response, null), context));
			//
			RequestExecutor _executor = WebMVC.processRequestMapping(context);
			if (_executor != null) {
				IView _view = _executor.execute();
				if (_view != null) {
					_view.render();
					return;
				}
			} else if (WebMVC.getConfig().isConventionModel()
					&& StringUtils.trimToEmpty(WebMVC.getConfig().getUrlSuffix()).endsWith(WebContext.getWebRequestContext().getSuffix())) {
				// 先尝试调用自定义的约定优于配置的URL请求映射处理过程
				if (WebMVC.getConfig().getErrorHandlerClassImpl() != null) {
					IView _view = WebMVC.getConfig().getErrorHandlerClassImpl().onConvention(context.getRequestMapping());
					if (_view != null) {
						_view.render();
						return;
					}
				}
				// 采用系统默认方式处理约定优于配置的URL请求映射
				String[] _fileTypes = { ".jsp", ".ftl" };
				File _targetFile = null;
				for (String _fileType : _fileTypes) {
					_targetFile = new File(getBaseViewFilePath(), context.getRequestMapping() + _fileType);
					if (_targetFile != null && _targetFile.exists()) {
						if (".jsp".equals(_fileType)) {
							new JspView().render();
							return;
						} else if (".ftl".equals(_fileType)) {
							new FreeMarkerView().render();
							return;
						}
					}
				}
			}
			// 到这儿就只能404了
			new HttpStatusView(HttpServletResponse.SC_NOT_FOUND).render();
		} catch (Exception e) {
			IWebErrorHandler _errorHandler = WebMVC.getConfig().getErrorHandlerClassImpl();
			if (_errorHandler != null) {
				_errorHandler.onError(e);
			} else {
				throw new ServletException(e);
			}
		} finally {
			WebContext.setContext(null);
			_stopWatch.stop();
			_LOG.info("请求[" + context.getRequestMapping() + "][" + request.getMethod() + "]处理完毕，耗时" + _stopWatch.getTime() + "ms");
		}
	}

	/**
	 * @param request
	 * @return 尝试处理RESTFul请求Method包装
	 */
	protected HttpServletRequest wrapperRequestForREST(HttpServletRequest request) {
		if (WebMVC.isInited() && WebMVC.getConfig().isRestfulModel()) {
			String paramValue = request.getParameter(methodParam);
			if ("POST".equals(request.getMethod()) && StringUtils.isNotBlank(paramValue)) {
				String method = paramValue.toUpperCase(Locale.ENGLISH);
				return new HttpMethodRequestWrapper(request, method);
			}
		}
		return request;
	}

	public String getMethodParam() {
		return methodParam;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getBaseViewFilePath() {
		return baseViewFilePath;
	}

}