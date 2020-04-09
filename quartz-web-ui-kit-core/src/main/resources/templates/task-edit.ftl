<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>编辑定时任务</title>
    <#include 'common/script.ftl'>
</head>
<body>
<div class="uk-container">
    <hr>
    <h1 style="text-align: center">编辑定时任务</h1>
    <div style="text-align: center">
        <a class="uk-link" href="${request.contextPath}/quartz/kit/task/list">返回任务列表</a>
    </div>
    <hr>
    <form class="uk-form-horizontal" id="edit-task-form">
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-id">任务ID</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-id" type="text" readonly name="taskId" value="${task.taskId}">
            </div>
        </div>
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-class">任务类</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-class" type="text" readonly name="taskClass"
                       value="${task.taskClass}">
            </div>
        </div>
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-group">任务分组</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-group" type="text" readonly name="taskGroup"
                       value="${task.taskGroup}">
            </div>
        </div>
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-group">任务描述</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-group" type="text" name="taskDescription"
                       value="${task.taskDescription?if_exists}">
            </div>
        </div>
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-group">任务表达式</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-group" type="text" name="taskExpression"
                       value="${task.taskExpression}">
            </div>
        </div>
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-group">任务参数</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-group" type="text" name="taskParameter"
                       value="${task.taskParameter?if_exists}">
            </div>
        </div>
        <div class="uk-margin">
            <div class="uk-form-label">任务状态</div>
            <div class="uk-form-controls">
                <label><input class="uk-radio" value="1" type="radio" name="taskStatus"
                              <#if task.taskStatus == 1>checked</#if>> 启用</label>&nbsp;&nbsp;
                <label><input class="uk-radio" value="0" type="radio" name="taskStatus"
                              <#if task.taskStatus == 0>checked</#if>> 禁用</label>
            </div>
        </div>
        <div class="uk-margin">
            <div class="uk-form-controls">
                <button type="button" class="uk-button uk-button-default" id="btn-submit-edit">提交</button>
            </div>
        </div>
    </form>
</div>
<script type="text/javascript">
    $('#btn-submit-edit').on('click', function () {
        let form = $('#edit-task-form');
        let taskId = '${task.taskId}';
        let url = '${request.contextPath}/quartz/kit/task/edit';
        let formData = form.serializeArray();
        let data = {};
        $.each(formData, function () {
            data[this.name] = this.value;
        });
        $.ajax({
            type: 'POST',
            url: url,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(data),
            success: function (response) {
                if (response && response.code === 200) {
                    UIkit.notification({
                        message: '<span uk-icon=\'icon: check\'></span> 更新定时任务' + taskId + '成功!',
                        status: 'success'
                    });
                } else {
                    UIkit.notification({
                        message: '更新定时任务' + taskId + '失败!',
                        status: 'danger'
                    });
                }
            },
            dataType: 'json'
        });
    });
</script>
</body>
</html>