package com.cafe24.dk4750.miniMarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.cafe24.dk4750.miniMarket.vo.ReportMByC;

public class ReportMByCController {

	@GetMapping("/mByCReport")
	   public String addReport() {
	      return "mByCReport";
	   }
	   @PostMapping("mByCReport")
	   public String addReport(ReportMByC reportMByC) {
	      System.out.println(reportMByC);
	      return "redirect:/mByCReport";
	   }
}
