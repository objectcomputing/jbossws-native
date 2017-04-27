A few things that are not so obvious when working with eventing:

1.) In order to dispatch you'll need to know the wsa:Action the EventSink listens on.
    It's required to build the wse:Delivery EPR.
    The WS-Eventing impl. actualy uses a naming convention:
    It takes the EventSourceNS and appends "/Notification" to it, i.e:

    EventDispatcher delegate = (EventDispatcher) iniCtx.lookup(EventingConstants.DISPATCHER_JNDI_NAME);
    delegate.dispatch(new URI("http://www.jboss.org/sysmon/SystemInfo"), payload);

2.) Where do it get the EventSourceNS from?
    Answer: There is an MBean in place that allows browsing of the registered EventSources:

    jboss.ws:service=SubscriptionManager,module=eventing

    The 'showEventsourceTable()' dumps the EventSource names and namespaces.

3.) I'll dispatch but never receive it at the sink.
    Answer: You are probably using the wrong wse:Delivery EPR.
    Take a look at the sample test case. The EPR is build like this:

    EndpointReferenceType notifyEPR = new EndpointReferenceType();
    AttributedURIType attURI = new AttributedURIType();
    attURI.setValue("http://localhost:8080/jaxws-samples-wseventing-sink/EventSink");
    notifyEPR.setAddress(attURI);
    delivery.setNotifyTo(notifyEPR);

    Whereas 'http://localhost:8080/jaxws-samples-wseventing-sink/EventSink' is the event sink address
    that's deployed as an EJB3 web service.

    