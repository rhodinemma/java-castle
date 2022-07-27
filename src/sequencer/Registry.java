package sequencer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/*
*   A Java RMI registry is a simplified name service that allows clients to get
*   a reference (a stub) to a remote object. it provides remote communication
*   between Java programs
* */

public class Registry {

    public Registry() {
        System.out.println("RMI server started");
        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }
    }

}


