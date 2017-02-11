/**
 *
 */
package com.github.opensource21.vsynchistory.controller;

import javax.annotation.Resource;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.opensource21.vsynchistory.service.api.GitService;

/**
 * Controller welcher das Git-Log anzeigt.
 * 
 * @author niels
 *
 */
@Controller
public class ShowLogController {

    @Resource
    private GitService gitService;

    @RequestMapping(value = { "/", "showLog" }, method = RequestMethod.GET)
    public String showlog(Model model) throws GitAPIException {
        model.addAttribute("logmessages", gitService.getLogMessages(100));
        return "showLog";
    }

}
