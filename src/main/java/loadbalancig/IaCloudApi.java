/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

* IaCloud is the class that connect to the sever model LLM Via HTTP/HTTPS 
and return the outcaomes of optimized algorithm depending on initial
confic or the config during the time


* Typical usage:
 * <pre>{@code
 * ConfigManager config = new ConfigManager("config.properties");
 * double deadline = config.getDouble("deadline", 10);
 * boolean debugMode = config.getBoolean("debug");
 * }</pre>
 *
 * @author Ben Halima Anouar
 * @version 1.0
 * 

 */

package loadbalancig;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *
 * @author aaaaa
 */
public class IaCloudApi {
    
        static  private HttpClient client = HttpClient.newHttpClient();
        static private JsonObject optimizedAlgorithm=null;
        
        private static String  formBody=null;
        static ConfigManager config = null;
        private static int[] metrics;

    public static int[] getMetrics() {
        return metrics;
    }

    public static void setMetrics(int[] metrics) {
        IaCloudApi.metrics = metrics;
    }
        private  final static int metricCount=9;
        private   static int AUTO=0;
        
        public static void adjustMetrics(int pos) {
            
            if (metrics[pos]==0)metrics[pos]=1;
            else metrics[pos]=0;
            
            
            
            
    }
        

        public static JsonObject getOptimizedAlgorithm() {
        return optimizedAlgorithm;
    }
        
    public static int getOptimizedAlgorithmAsNumber() {
        
        String policy = optimizedAlgorithm.get("result").getAsString();
        
          switch (policy) {
            case "DynamicRR":
                return  LoadBalancingPolicy.DYNAMIC_ROUND_ROBIN;
            case "RR":
                return  LoadBalancingPolicy.ROUND_ROBIN;   
            case "HoneyBeeForaging":
                return LoadBalancingPolicy.HONEYBEE;
            case "PALB":
                return LoadBalancingPolicy.PALB;
            case "AntColony":
                return LoadBalancingPolicy.ANT_COLONY;
            case "WRR":
                return LoadBalancingPolicy.W_ROUND_ROBIN;
            case "Min-Min":
                return LoadBalancingPolicy.MIN_MIN; 
            case "Max-Min":
                return LoadBalancingPolicy.MAX_MIN; 
            case "JoinIdleQueue":
                return LoadBalancingPolicy.JOIN_IDLE_QUEUE; 
            case "ActiveClustering":
                return LoadBalancingPolicy.ACTIVE_CLUSTERING;
            
                
    

            default:
                 return 0;
        }
        
        
       
    }
        

    public static void setOptimizedAlgorithm(JsonObject optimizedAlgorithm) {
        IaCloudApi.optimizedAlgorithm = optimizedAlgorithm;
    }
    
    public static String getValue( String value){
        
     return config.getString(value).trim().toLowerCase();
     
    }
     
    public static void fromPropretiesToMetrics(Map<String, String> metricsMap){
        
        String metricsenum []= {"Performance","Throughtput","Overhead",
            "Tolerant","MigrationTime","ResponseTime",
        "RessourceUtilization","Scalability","PowerSaving"};
        
        
        
        for (int i = 0; i < metricsenum.length; i++) {
            String valueMetric= getValue(metricsenum[i]);
            //System.out.println(i+"valueMetric "+ valueMetric);
                
                if (i<metricCount){
                if (valueMetric.equals("yes"))
                    metrics[i] = 1;
                else 
                    metrics[i] = 0;
                 }
            
        }
            
    } 
     
    
    public static boolean ifMetricsChanged(int[] Newmetrics){
        var changed= false;
        if (metrics == null || Newmetrics == null) return false;
        if (metrics.length != Newmetrics.length) return true;

        for (int i = 0; i < metrics.length; i++) {
            if (metrics[i] != Newmetrics[i]) {
                changed= true;
                break;
            }
        }
        return changed;
   
        
        
        
    }
        
        private static  void loadPropreties() throws IOException{
            
            config = new ConfigManager("config.properties");
            Map<String, String> metricsMap = new LinkedHashMap<>(); // keep insertion order
            LoadBalancingPolicy.API=config.getString("api").trim();
            
            metrics = new int[metricCount];
            System.out.println("=======================Load file config ==============================");
            config.printAll();
           System.out.println("=======================------------------==============================");
            

            for (String key : config.getAllKeys()) {
                
                String value = config.getString(key).trim().toLowerCase();
               
                metricsMap.put(key, value);
            }
            
            fromPropretiesToMetrics(metricsMap);
           
            
            //System.out.println("------------------------"+ Arrays.toString(metrics));
            
        }
        
        
        public static void init () throws IOException{
            
            
            if(IaCloudApi.AUTO==0)
            loadPropreties();
            
            IaCloudApi.AUTO++;
            
            
            formBody = "{\"array_param\":" + Arrays.toString(metrics)+"}";
            
             
             
            //System.out.println("Auto"+ IaCloudApi.AUTO +"From bOdy "+formBody);
            connect();
        }
        
    private static JsonObject connect() throws IOException{
        HttpRequest request = null;
        optimizedAlgorithm=null;
        request = HttpRequest.newBuilder()
                .uri(URI.create(LoadBalancingPolicy.API))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build(); //Logger.getLogger(IaCloudApi.class.getName()).log(Level.SEVERE, null, ex);
        HttpResponse<String> response;
        
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("Title: " + response.body());
            optimizedAlgorithm = JsonParser.parseString(response.body()).getAsJsonObject();
            /*
            System.out.println("Title: " + jsonObject.get("title").getAsString());
            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body:\n" + response.body());
            */
            
           
            
        } catch (InterruptedException ex) {
         // Logger.getLogger(IaCloudApi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.err.println(ex);
        }
         return  optimizedAlgorithm;
       
      
    }    
        
        
}
