package com.payout.psg.http;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class InfoController {

	@GetMapping("/infos")
	public String info() {
		return "Informations sur le service";
	}
	
	@GetMapping("/metrics")
	public Map<String,Object> getMetrics() {
		
		Map<String,Object> memory = new HashMap<String,Object>();
		
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		
		memory.put("initial", (double)memoryMXBean.getHeapMemoryUsage().getInit() /1073741824);
		memory.put("usedheap", (double)memoryMXBean.getHeapMemoryUsage().getUsed() /1073741824);
		memory.put("maxheap", (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824);
		memory.put("commitedMemory", (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824);
		
		return memory;
	}
}
