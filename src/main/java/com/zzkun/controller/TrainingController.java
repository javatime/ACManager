package com.zzkun.controller;

import com.alibaba.fastjson.JSON;
import com.zzkun.model.Contest;
import com.zzkun.model.Stage;
import com.zzkun.model.Training;
import com.zzkun.model.User;
import com.zzkun.service.TrainingService;
import com.zzkun.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Administrator on 2016/7/20.
 */
@Controller
@RequestMapping("/training")
public class TrainingController {

    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

    @Autowired private TrainingService trainingService;

    @Autowired private UserService userService;


    @RequestMapping("/list")
    public String mylist(Model model, HttpSession session) {
        List<Training> allTraining = trainingService.getAllTraining();
        model.addAttribute("allList", allTraining);
        model.addAttribute("trainingAddUserList", userService.getUserInfoByTList(allTraining));
        model.addAttribute("ujointMap", trainingService.getUserRelativeTraining((User) session.getAttribute("user")));
        return "trainingsetlist";
    }

    @RequestMapping("/AddGame")
    public String addGame(Model model) {
        model.addAttribute("allList", trainingService.getAllTraining());
        return "AddGame";
    }

    @RequestMapping("/detail/{id}")
    public String detail(@PathVariable Integer id,
                         Model model,
                         HttpSession session) {
        List<Stage> stageList = trainingService.getAllStageByTrainingId(id);
        model.addAttribute("info", trainingService.getTrainingById(id));
        model.addAttribute("stageList", stageList);
        model.addAttribute("stageAddUserList", userService.getUserInfoBySList(stageList));
        session.setAttribute("trainingId", id);
        return "stagelist";
    }

    @RequestMapping("/stage/{id}")
    public String stage(@PathVariable Integer id,
                        Model model,
                        HttpSession session) {
        List<Contest> contestList = trainingService.getContestByStageId(id);
        model.addAttribute("contestList", contestList);
        model.addAttribute("trainingId", trainingService.getStageById(id).getTrainingId());
        session.setAttribute("stageId", id);
        return "gamelist";
    }

    ///////// ajax

    @RequestMapping(value = "/getstatge",produces = "text/html;charset=UTF-8" )
    @ResponseBody
    public String getstatge(@RequestParam Integer id) {
        List<Stage> stages = trainingService.getAllStageByTrainingId(id);
        String s = JSON.toJSONString(stages);
        logger.info("获取集训阶段信息：{}", s);
        return s;
    }

    @RequestMapping(value = "/doAddTraining", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String doAddTraining(@RequestParam String name,
                                @RequestParam String startDate,
                                @RequestParam String endDate,
                                @RequestParam(required = false) String remark,
                                HttpSession session) {
        logger.info("收到添加Training请求：name = [" + name + "], startDate = [" + startDate + "], endDate = [" + endDate + "], remark = [" + remark + "]");
        try {
            User user = (User) session.getAttribute("user");
            Training training = new Training();
            training.setName(name);
            training.setStartDate(LocalDate.parse(startDate));
            training.setEndDate(LocalDate.parse(endDate));
            training.setRemark(remark);
            training.setAddTime(LocalDateTime.now());
            training.setAddUid(user.getId());
            trainingService.addTraining(training);
            return "添加成功";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "添加失败！";
    }


    @RequestMapping(value = "doAddStage", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String doAddStage(@RequestParam String name,
                             @RequestParam String startDate,
                             @RequestParam String endDate,
                             @RequestParam(required = false) String remark,
                             HttpSession session) {
        logger.info("收到添加Stage请求：name = [" + name + "], startDate = [" + startDate + "], endDate = [" + endDate + "], remark = [" + remark + "]");
        try {
            User user = (User) session.getAttribute("user");
            Stage stage = new Stage();
            stage.setName(name);
            stage.setStartDate(LocalDate.parse(startDate));
            stage.setEndDate(LocalDate.parse(endDate));
            stage.setRemark(remark);
            stage.setTrainingId((Integer) session.getAttribute("trainingId"));
            stage.setAddTime(LocalDateTime.now());
            stage.setAddUid(user.getId());
            trainingService.addStage(stage);
            return "添加成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "添加失败！";
    }

}
