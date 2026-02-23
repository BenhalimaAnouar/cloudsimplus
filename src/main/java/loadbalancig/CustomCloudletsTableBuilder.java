/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;

import java.util.List;
import java.util.function.Function;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.MarkdownTableColumn;
import org.cloudsimplus.builders.tables.TableColumn;
import org.cloudsimplus.cloudlets.Cloudlet;

/**
 *
 * @author aaaaa
 */
public class CustomCloudletsTableBuilder extends CloudletsTableBuilder {

    public CustomCloudletsTableBuilder(List<? extends Cloudlet> list) {
        super(list);

        //Function<Cloudlet, Object> cloudletTypeFunction = cloudlet -> ((AdaptiveBroker)cloudlet.getBroker()).getPolicy();
        // 2. Define the custom TableColumn object
        // You can specify title, alignment, and type (optional).
        TableColumn customColumn = new MarkdownTableColumn("-----------------Policy-----------");

        int lastIndex = getTable().getColumns().size();
        addColumn(customColumn,
                (Cloudlet c) -> {  // lambda that returns the value to show for each cloudlet
                    if (c instanceof CustomCloudlet) {
                        return ((CustomCloudlet) c).getPolicy();
                    }
                    return "N/A";
                }, lastIndex + 1);

    }

    private Object getPolicyValue(final Cloudlet c) {
        if (c instanceof CustomCloudlet pc) {
            return pc.getPolicy();
        }
        return "N/A";
    }

}
