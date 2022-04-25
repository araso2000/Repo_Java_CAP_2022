package eje5;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;

public class VmAllocationPolicyRandom extends VmAllocationPolicySimple {
	 
	public VmAllocationPolicyRandom(List<? extends Host> list){
		super(list);
	}
	
	@Override
	public boolean allocateHostForVm(Vm vm) {
		int requiredPes = vm.getNumberOfPes();
		boolean result = false;
		int tries = 0;
		List<Integer> freePesTmp = new ArrayList<Integer>();
		for (Integer freePes : getFreePes()) {
			freePesTmp.add(freePes);
		}
		if (!getVmTable().containsKey(vm.getUid())) { // if this vm was not created
			do {// we still trying until we find a host or until we try all of them
				
				List<Integer> yaVisitados = new ArrayList<Integer>();
				
				int idx;
				
				do {
					idx = (int)Math.random()*(getHostList().size()+1);
				}while(yaVisitados.contains(idx));
				
				Host host = getHostList().get(idx);
				result = host.vmCreate(vm);
				if (result) { // if vm were succesfully created in the host
					getVmTable().put(vm.getUid(), host);
					getUsedPes().put(vm.getUid(), requiredPes);
					getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
					result = true;
					break;
				} else {
					yaVisitados.add(idx);
				}
				tries++;
			} while (!result && tries < getFreePes().size());
		}
		return result;
	}
}