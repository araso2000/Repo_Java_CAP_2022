package eje6;

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


public class Eje6 {
	private int indice = 0;
	
    public Eje6(){
    	
    }

    public void run(){

        this.initCloudSim();

        this.createDataCenter();
        
        DatacenterBroker broker1 = this.createResources(1);
        DatacenterBroker broker2 = this.createResources(2);
        DatacenterBroker broker3 = this.createResources(3);
        
        this.simulate();
        
        this.printCloudletsResults(broker1);
        this.printCloudletsResults(broker2);
        this.printCloudletsResults(broker3);
        
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
        List<Pe> processorElements_1 = new ArrayList<Pe>();
        int mips_1 = 2000;
        for(int ii=0;ii<2;ii++) {
        	processorElements_1.add(new Pe((processorElements_1.size()),new PeProvisionerSimple(mips_1)));
        }
        
        List<Pe> processorElements_2 = new ArrayList<Pe>();
        int mips_2 = 2400;
        for(int ii=0;ii<4;ii++) {
        	processorElements_2.add(new Pe((processorElements_2.size()),new PeProvisionerSimple(mips_2)));
        }

		int ram_1 = 8192;
		long almacenamiento_1 = 1000000;
		long anchoBanda_1 = 10000;
		
		int ram_2= 24576;
		long almacenamiento_2 = 2000000;
		long anchoBanda_2 = 10000;
		
        List<Host> hosts = new ArrayList<Host>();
        for(int ii=0;ii<16;ii++) {
        	hosts.add(new Host((hosts.size()), new RamProvisionerSimple(ram_1),new BwProvisionerSimple(anchoBanda_1), almacenamiento_1,processorElements_1, new VmSchedulerSpaceShared(processorElements_1)));
        }
        
        for(int ii=0;ii<4;ii++) {
        	hosts.add(new Host((hosts.size()), new RamProvisionerSimple(ram_2),new BwProvisionerSimple(anchoBanda_2), almacenamiento_2,processorElements_2, new VmSchedulerSpaceShared(processorElements_2)));
        }
        
        LinkedList<Storage> storageList = new LinkedList<Storage>();

        String arquitectura = "x86";
		String so = "Linux";
		String vmm = "Xen";
		String nombre = "Datacenter_0";
		double zonaHoraria = 2.0;
		double costePorSeg = 0.01;
		double costePorMem = 0.01;
		double costePorAlm = 0.01;
		double costePorBw = 0.01;
		
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arquitectura,so, vmm, hosts, zonaHoraria, costePorSeg,costePorMem, costePorAlm, costePorBw);
		Datacenter datacenter = null;
		
        try {
            datacenter = new Datacenter(nombre, characteristics,new VmAllocationPolicyRandom(hosts),storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine(">> ERROR creating datacenter");
        }
    }

    private DatacenterBroker createResources(int index) {
    	String temp = "broker" + this.indice;
    	DatacenterBroker broker = null;
    	
    	try {
            broker = new DatacenterBroker(temp);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.printLine(">> ERROR creating broker");
        }
    	
    	if(index==1) {
    		List<Vm> virtualMachines_A = new ArrayList<Vm>();
            for (int jj = 0; jj < 8; jj++) {
            	virtualMachines_A.add(new Vm((virtualMachines_A.size()), broker.getId(),2400, 1, 3072, 1000, 120000, "Xen", new CloudletSchedulerSpaceShared()));
            }
                
            broker.submitVmList(virtualMachines_A);
    	}else if(index==2) {
    		List<Vm> virtualMachines_B = new ArrayList<Vm>();
            for (int jj = 0; jj < 16; jj++) {
            	virtualMachines_B.add(new Vm((virtualMachines_B.size()), broker.getId(),2000, 1, 2048, 1000, 80000, "Xen", new CloudletSchedulerSpaceShared()));
            }
                
            broker.submitVmList(virtualMachines_B);
    	}else {
    		List<Vm> virtualMachines_C = new ArrayList<Vm>();
            for (int jj = 0; jj < 24; jj++) {
            	virtualMachines_C.add(new Vm((virtualMachines_C.size()), broker.getId(),1800, 1, 1024, 1000, 60000, "Xen", new CloudletSchedulerSpaceShared()));
            }
                
            broker.submitVmList(virtualMachines_C);
    	}
        
    	/*List<Cloudlet> cloudlets = new ArrayList<Cloudlet>();

        UtilizationModel utilizationModel = new UtilizationModelFull();
            
        for(int jj=0;jj<15;jj++) {
            Cloudlet temp1 = new Cloudlet(cloudlets.size(),450000000,1,1048576,1572864,utilizationModel,utilizationModel,utilizationModel);
            temp1.setUserId(broker.getId());
            cloudlets.add(temp1);
        }
            
        broker.submitCloudletList(cloudlets);*/
    	
        this.indice++;
    	return broker;
    }

    private void simulate(){
        Log.printLine(">> Iniciando simulacion...");
        CloudSim.startSimulation();
        Log.printLine(">> Simulacion en curso...");
        CloudSim.stopSimulation();
        Log.printLine(">> Simulacion finalizada.");
    }

    private void printCloudletsResults(DatacenterBroker broker) {
        this.printCloudletList(broker.getCloudletReceivedList());
    }
}
