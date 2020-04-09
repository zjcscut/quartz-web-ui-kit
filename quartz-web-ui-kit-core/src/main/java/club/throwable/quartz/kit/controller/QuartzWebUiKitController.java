package club.throwable.quartz.kit.controller;

import club.throwable.quartz.kit.service.QuartzWebUiKitService;
import club.throwable.quartz.kit.service.vo.AddScheduleTaskVo;
import club.throwable.quartz.kit.service.vo.EditScheduleTaskVo;
import club.throwable.quartz.kit.service.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 12:50
 */
@Controller
@RequestMapping(path = "/quartz/kit/task")
public class QuartzWebUiKitController {

    @Autowired
    private QuartzWebUiKitService quartzWebUiKitService;

    @GetMapping(path = "/list")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("task-list");
        mav.addObject("tasks", quartzWebUiKitService.getAllTasks());
        return mav;
    }

    @GetMapping(path = "/trigger/{taskId}")
    @ResponseBody
    public Response<?> trigger(@PathVariable(name = "taskId") String taskId) throws Exception {
        quartzWebUiKitService.triggerByTaskId(taskId);
        return Response.succeed();
    }

    @GetMapping(path = "/start/{taskId}")
    @ResponseBody
    public Response<?> start(@PathVariable(name = "taskId") String taskId) {
        quartzWebUiKitService.startByTaskId(taskId);
        return Response.succeed();
    }

    @GetMapping(path = "/stop/{taskId}")
    @ResponseBody
    public Response<?> stop(@PathVariable(name = "taskId") String taskId) throws Exception {
        quartzWebUiKitService.stopByTaskId(taskId);
        return Response.succeed();
    }

    @GetMapping(path = "/edit/{taskId}")
    public ModelAndView editTaskPage(@PathVariable(name = "taskId") String taskId) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("task-edit");
        mav.addObject("task", quartzWebUiKitService.selectByTaskId(taskId));
        return mav;
    }

    @GetMapping(path = "/add")
    public ModelAndView addTaskPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("task-add");
        mav.addObject("taskGroup", quartzWebUiKitService.getTaskGroup());
        return mav;
    }

    @PostMapping(path = "/edit")
    @ResponseBody
    public Response<?> editTask(@Validated @RequestBody EditScheduleTaskVo vo) {
        quartzWebUiKitService.editTask(vo);
        return Response.succeed();
    }

    @PostMapping(path = "/add")
    @ResponseBody
    public Response<?> addTask(@Validated @RequestBody AddScheduleTaskVo vo) {
        quartzWebUiKitService.addTask(vo);
        return Response.succeed();
    }

    @GetMapping(path = "/delete/{taskId}")
    @ResponseBody
    public Response<?> deleteTask(@PathVariable(name = "taskId") String taskId) throws Exception {
        quartzWebUiKitService.deleteTaskByTaskId(taskId);
        return Response.succeed();
    }
}
