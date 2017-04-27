/**
 * 
 */
package org.jboss.test.ws.jaxws.jbws2565;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.wsf.spi.annotation.TransportGuarantee;
import org.jboss.wsf.spi.annotation.WebContext;

@Stateless
@PermitAll
@WebService
(
   name = "MyWebService",
   targetNamespace = "http://my.services.web",
   serviceName = "MyWebServiceName"
)
@WebContext
(
   contextRoot = "/jaxws-jbws2565-wrong",
   transportGuarantee = TransportGuarantee.NONE,
   authMethod = "NONE" // this is wrong value, deployment should be rejected
)
@SOAPBinding
(
   use = SOAPBinding.Use.LITERAL,
   style = SOAPBinding.Style.DOCUMENT,
   parameterStyle = SOAPBinding.ParameterStyle.WRAPPED
)
public final class MyWebServiceBeanWrong
{
   @WebMethod
   @PermitAll
   public final String doStuff()
   {
       return "i've done stuff";
   }
}
