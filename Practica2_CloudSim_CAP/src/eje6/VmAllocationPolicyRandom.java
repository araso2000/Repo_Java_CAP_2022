package eje6;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;

public class VmAllocationPolicyRandom extends VmAllocationPolicySimple {
	
	private Random random = new Random(java.time.Clock.systemUTC().millis());
	 
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
		if (!getVmTable().containsKey(vm.getUid())) {
			do {
				//Creamos una lista que almacene los host ya visitados y que no pudieron alojar la VM
				List<Integer> yaVisitados = new ArrayList<Integer>();
				int idx=0;
				//Dentro del bucle, buscar de manera RANDOM un host que no haya sido ya visitado
				do {
					idx = random.nextInt(getHostList().size());
				}while(yaVisitados.contains(idx));				
				
				Host host = getHostList().get(idx);
				result = host.vmCreate(vm);
				
				//Si el host elegido alojar a la VM, el bucle terminará. Si no, será añadido a la lista de
				//ya visitados
				if (result) {
					getVmTable().put(vm.getUid(), host);
					getUsedPes().put(vm.getUid(), requiredPes);
					getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
					result = true;
					break;
				} else {
					yaVisitados.add(idx);
				}
				tries++;
			} while (!result || (tries < getHostList().size()));
		}
		return result;
	}
}

/*
 * 
 * @Override
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
				int moreFree = Integer.MIN_VALUE;
				int idx = -1;

				// we want the host with less pes in use
				for (int i = 0; i < freePesTmp.size(); i++) {
					if (freePesTmp.get(i) > moreFree) {
						moreFree = freePesTmp.get(i);
						idx = i;
					}
				}

				Host host = getHostList().get(idx);
				result = host.vmCreate(vm);

				if (result) { // if vm were succesfully created in the host
					getVmTable().put(vm.getUid(), host);
					getUsedPes().put(vm.getUid(), requiredPes);
					getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
					result = true;
					break;
				} else {
					freePesTmp.set(idx, Integer.MIN_VALUE);
				}
				tries++;
			} while (!result && tries < getFreePes().size());

		}

		return result;
	}
 * 
 * */
