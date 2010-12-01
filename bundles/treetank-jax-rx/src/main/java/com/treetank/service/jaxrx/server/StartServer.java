/**
 * 
 */
package com.treetank.service.jaxrx.server;

import org.jaxrx.StartJetty;

/**
 * This class starts the in JAX-RX embedded Jetty server.
 * 
 * @author Lukas Lewandowski, University of Konstanz
 * 
 */
public final class StartServer {

    /**
     * I'm a lazy constructor.
     */
    private StartServer() {

    }

    /**
     * This method starts the embedded Jetty server.
     * 
     * @param args
     *            Not used parameter.
     */
    public static void main(final String[] args) {
        int port=8093;
        if(args!=null && args.length>0){
            port = Integer.parseInt(args[0]);
        }
        System.setProperty("org.jaxrx.systemPath",
            "com.treetank.service.jaxrx.implementation.TreeTankMediator");
        System.setProperty("org.jaxrx.systemName", "treetank");
        new StartJetty(port);
    }
}
