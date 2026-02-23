/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;

import org.cloudsimplus.cloudlets.CloudletSimple;

/**
 *
 * @author aaaaa
 */
public class CustomCloudlet extends CloudletSimple {

    private String policy;

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public CustomCloudlet(long length, int pesNumber) {
        super(length, pesNumber);
    }

}
