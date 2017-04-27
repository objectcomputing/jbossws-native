/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package javax.xml.ws;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>Action</code> annotation allows explicit association of <code>Action</code> 
 * message addressing property with <code>input</code>, <code>output</code>, and 
 * <code>fault</code> messages of the mapped WSDL operation. 
 * <p>
 * This annotation can be specified on each method of a service endpoint interface. 
 * For such a method, the mapped operation in the generated WSDL 
 * contains explicit <code>wsaw:Action</code> attribute on the WSDL <code>input</code>, 
 * <code>output</code> and <code>fault</code> messages of the WSDL <code>operation</code> 
 * based upon which attributes of the <code>Action</code> annotation have been specified.
 * <p>
 * <b>Example 1</b>: Specify explicit values for <code>Action</code> message addressing property 
 * for <code>input</code> and <code>output</code> messages.
 * 
 * <pre>
 * &#64;javax.jws.WebService
 * public class AddNumbersImpl {
 *     &#64;javax.xml.ws.Action(
 *         input="http://example.com/inputAction",
 *         output="http://example.com/outputAction")
 *     public int addNumbers(int number1, int number2) {
 *         return number1 + number2;
 *     }
 * }
 * </pre>
 * 
 * The generated WSDL looks like:
 * <pre>
 *   &lt;definitions targetNamespace=&quot;http://example.com/numbers&quot; ...&gt;
 *   ...
 *     &lt;portType name=&quot;AddNumbersPortType&quot;&gt;
 *       &lt;operation name=&quot;AddNumbers&quot;&gt;
 *         &lt;input message=&quot;tns:AddNumbersInput&quot; name=&quot;Parameters&quot;
 *           wsaw:Action=&quot;http://example.com/inputAction&quot;/&gt;
 *        &lt;output message=&quot;tns:AddNumbersOutput&quot; name=&quot;Result&quot;
 *           wsaw:Action=&quot;http://example.com/outputAction&quot;/&gt;
 *       &lt;/operation&gt;
 *     &lt;portType&gt;
 *   ...
 *   &lt;definitions&gt;
 * </pre>
 * 
 * <p>
 * <b>Example 2</b>: Specify explicit value for <code>Action</code> message addressing property
 * for only the <code>input</code> message. The default values are used for the 
 * <code>output</code> message.
 * 
 * <pre>
 * &#64;javax.jws.WebService
 * public class AddNumbersImpl {
 *     &#64;javax.xml.ws.Action(input=&quot;http://example.com/inputAction&quot;)
 *     public int addNumbers(int number1, int number2) {
 *         return number1 + number2;
 *     }
 * }
 * </pre>
 * 
 * The generated WSDL looks like:
 * 
 * <pre>
 *   &lt;definitions targetNamespace=&quot;http://example.com/numbers&quot; ...&gt;
 *   ...
 *     &lt;portType name=&quot;AddNumbersPortType&quot;&gt;
 *       &lt;operation name=&quot;AddNumbers&quot;&gt;
 *         &lt;input message=&quot;tns:AddNumbersInput&quot; name=&quot;Parameters&quot;
 *           wsaw:Action=&quot;http://example.com/inputAction&quot;/&gt;
 *        &lt;output message=&quot;tns:AddNumbersOutput&quot; name=&quot;Result&quot;/&gt;
 *       &lt;/operation&gt;
 *     &lt;portType&gt;
 *   ...
 *   &lt;definitions&gt;
 * </pre>
 *
 * It is legitimate to specify an explicit value for <code>Action</code> message addressing property for
 * <code>output</code> message only. In this case, a default value of <code>wsaw:Action</code> is used 
 * for the <code>input</code> message.
 * 
 * <p>
 * <b>Example 3</b>: See <a href="FaultAction.html">FaultAction</a> annotation for an example of 
 * how to specify an explicit value for <code>Action</code> message addressing property for the 
 * <code>fault</code> message.
 * 
 * @see FaultAction
 *
 * @since JAX-WS 2.1
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {
   /**
    * Explicit value of <code>Action</code> message addressing property for the <code>input</code>
    * message of the operation. If the value is "", then no <code>wsaw:Action</code>
    * is generated.
    */
   String input() default "";

   /**
    * Explicit value of <code>Action</code> message addressing property for the <code>output</code>
    * message of the operation. If the value is "", then no <code>wsaw:Action</code>
    * is generated.
    */
   String output() default "";

   /**
    * Explicit value of <code>Action</code> message addressing property for the <code>fault</code>
    * message(s) of the operation. Each exception that is mapped to a fault and requires explicit
    * <code>Action</code> message addressing property, need to be specified as a value in this property 
    * using <a href="FaultAction.html">FaultAction</a> annotation.
    */
   FaultAction[] fault() default {};
}
