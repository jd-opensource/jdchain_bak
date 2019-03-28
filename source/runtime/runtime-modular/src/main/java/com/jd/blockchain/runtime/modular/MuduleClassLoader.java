package com.jd.blockchain.runtime.modular;

import java.net.URL;
import java.net.URLClassLoader;

public class MuduleClassLoader extends URLClassLoader {
    private ClassLoader parent;
    public MuduleClassLoader(URL[] urls, ClassLoader parent){
        super(urls, parent);
        this.parent = parent;
    }


    public  Class<?> loadClass(String name)
            throws ClassNotFoundException{
        if (name.equals("com.jd.blockchain.contract.model.ContractEventContext") ){
            return this.parent.loadClass(name);
        }
        return super.loadClass(name);

    }

}
