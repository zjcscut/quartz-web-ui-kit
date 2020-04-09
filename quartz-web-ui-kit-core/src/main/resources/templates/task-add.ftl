<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>添加定时任务</title>
    <#include 'common/script.ftl'>
</head>
<body>
<div class="uk-container">
    <hr>
    <h1 style="text-align: center">添加定时任务</h1>
    <div style="text-align: center">
        <a class="uk-link" href="${request.contextPath}/quartz/kit/task/list">返回任务列表</a>
    </div>
    <hr>
    <form class="uk-form-horizontal" id="add-task-form">
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-class">任务类</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-class" type="text" name="taskClass">
            </div>
        </div>
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-group">任务分组</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-group" readonly type="text" name="taskGroup"
                       value="${taskGroup}">
            </div>
        </div>
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-group">任务描述</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-group" type="text" name="taskDescription">
            </div>
        </div>
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-group">任务表达式</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-group" type="text" name="taskExpression">
            </div>
        </div>
        <div class="uk-margin">
            <label class="uk-form-label" for="input-task-group">任务参数</label>
            <div class="uk-form-controls">
                <input class="uk-input" id="input-task-group" type="text" name="taskParameter">
            </div>
        </div>
        <div class="uk-margin">
            <div class="uk-form-label">任务状态</div>
            <div class="uk-form-controls">
                <label><input class="uk-radio" value="1" type="radio" name="taskStatus" checked> 启用</label>&nbsp;&nbsp;
                <label><input class="uk-radio" value="0" type="radio" name="taskStatus"> 禁用</label>
            </div>
        </div>
        <div class="uk-margin">
            <div class="uk-form-controls">
                <button type="button" class="uk-button uk-button-default" id="btn-submit-add">提交</button>
            </div>
        </div>
    </form>
</div>
<script type="text/javascript">
    $('#btn-submit-add').on('click', function () {
        let form = $('#add-task-form');
        let formData = form.serializeArray();
        let data = {};
        let url = '${request.contextPath}/quartz/kit/task/add';
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
                    window.location.href = '${request.contextPath}/quartz/kit/task/list';
                } else {
                    UIkit.notification({
                        message: '添加定时任务失败!原因:' + response.message,
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