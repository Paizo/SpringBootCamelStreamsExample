package com.paizo.balance.controller;

import com.paizo.balance.service.impl.UtilityServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.String.format;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    ProducerTemplate producerTemplate;

	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}

    @GetMapping("/uploadCSV")
    public String csvFileUpload() {
        return "uploadCSV";
    }

    @GetMapping("/uploadPRN")
    public String prnFileUpload() {
        return "uploadPRN";
    }

	@PostMapping("/uploadCSV")
	public String csvFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {

	    try (
                InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream(), UtilityServiceImpl.DEFAULT_INPUT_CHARSET);
                BufferedReader in = new BufferedReader(inputStreamReader)
            ) {

            producerTemplate.sendBody("direct:personCSVStream", in);
        }

        redirectAttributes.addFlashAttribute("message", format("File upload completed [%s]", file.getOriginalFilename()));

		return "redirect:/uploadStatus";
	}

    @PostMapping("/uploadPRN")
    public String prnFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {

	    try (
                InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream(), UtilityServiceImpl.DEFAULT_INPUT_CHARSET);
	            BufferedReader in = new BufferedReader(inputStreamReader)
            ) {

            producerTemplate.sendBody("direct:personPRNStream", in);
        }

        redirectAttributes.addFlashAttribute("message", format("File upload completed [%s]", file.getOriginalFilename()));

        return "redirect:/uploadStatus";
    }

	@GetMapping("/uploadStatus")
	public String csvUploadStatus() {
		return "uploadStatus";
	}

	@GetMapping("/errorPage")
	public String genericErrorPage() {
		return "errorPage";
	}
}