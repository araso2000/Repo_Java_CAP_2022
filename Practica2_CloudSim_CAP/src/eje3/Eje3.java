package eje3;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class Eje3 {
	private int indice = 0;

    public Eje3(){}

    public void run(){

        this.initCloudSim();

        this.createDataCenter();

        int numUsers=10;
    	
        DatacenterBroker broker = null;
    	for(int ii=0;ii<numUsers;ii++) {
    		broker = this.createResources();
    	}

        this.simulate();

        this.printCloudletsResults(broker);

    }

    private void initCloudSim(){
        Log.printLine(">> Initializing cloudsim...");
        int num_user = 10; // number of cloud users
        Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
        boolean traceFlag = false; // trace events
        CloudSim.init(num_user, calendar, traceFlag);
        Log.printLine(">> Cloudsim ready!");
    }

    private void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
                + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId()
                        + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent
                        + dft.format(cloudlet.getActualCPUTime()) + indent
                        + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent
                        + dft.format(cloudlet.getFinishTime()));
            }
        }
    }

    private void createDataCenter(){
        List<Pe> processorElements = new ArrayList<Pe>();
        int mips = 1200;
        for(int ii=0;ii<4;ii++) {
        	processorElements.add(new Pe((processorElements.size()),new PeProvisionerSimple(mips)));
        }

        int hostId = 0;
		int ram = 24576;
		long almacenamiento = 2000000;
		long anchoBanda = 10000;
		
        List<Host> hosts = new ArrayList<Host>();
        for(int ii=0;ii<5;ii++) {
        	hosts.add(new Host((hosts.size()), new RamProvisionerSimple(ram),new BwProvisionerSimple(anchoBanda), almacenamiento,processorElements, new VmSchedulerSpaceShared(processorElements)));
        }
        
        LinkedList<Storage> storageList = new LinkedList<Storage>();

        String arquitectura = "x86";
		String so = "Linux";
		String vmm = "Xen";
		String nombre = "Datacenter_0";
		double zonaHoraria = 2.0;
		double costePorSeg = 0.01;
		double costePorMem = 0.005;
		double costePorAlm = 0.003;
		double costePorBw = 0.005;
		
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arquitectura,so, vmm, hosts, zonaHoraria, costePorSeg,costePorMem, costePorAlm, costePorBw);
		Datacenter datacenter = null;
		
        try {
            datacenter = new Datacenter(nombre, characteristics,
                    new VmAllocationPolicySimple(hosts),
                    storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine(">> ERROR creating datacenter");
        }
    }

    private DatacenterBroker createResources() {
    	String temp = "broker" + this.indice;
    	DatacenterBroker broker = null;
    	
    	try {
            broker = new DatacenterBroker(temp);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.printLine(">> ERROR creating broker");
        }
        
    	List<Vm> virtualMachines = new ArrayList<Vm>();
        for (int jj = 0; jj < 3; jj++) {
            virtualMachines.add(new Vm((virtualMachines.size()), broker.getId(),600, 2, 4096, 1000, 20000, "Xen", new CloudletSchedulerSpaceShared()));
        }
            
        broker.submitVmList(virtualMachines);
            
        List<Cloudlet> cloudlets = new ArrayList<Cloudlet>();

        UtilizationModel utilizationModel = new UtilizationModelFull();
            
        for(int jj=0;jj<15;jj++) {
            Cloudlet temp1 = new Cloudlet(cloudlets.size(),450000000,1,1048576,1572864,utilizationModel,utilizationModel,utilizationModel);
            temp1.setUserId(broker.getId());
            cloudlets.add(temp1);
        }
            
        broker.submitCloudletList(cloudlets);
    	
        this.indice++;
    	return broker;
    }

    private void simulate(){
        Log.printLine(">> Iniciando simulación...");
        CloudSim.startSimulation();
        Log.printLine(">> Simulación en curso...");
        CloudSim.stopSimulation();
        Log.printLine(">> Simulación finalizada.");
    }

    private void printCloudletsResults(DatacenterBroker broker) {
        this.printCloudletList(broker.getCloudletReceivedList());
    }
}
