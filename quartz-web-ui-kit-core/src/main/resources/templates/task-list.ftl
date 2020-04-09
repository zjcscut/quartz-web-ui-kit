<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>定时任务列表</title>
    <#include 'common/script.ftl'>
</head>
<body>
<div class="uk-container uk-container-expand">
    <hr>
    <h1 style="text-align: center">定时任务管理</h1>
    <form>
        <div class="uk-form-controls">
            <a type="button" class="uk-button uk-button-default" target="_blank"
               href="${request.contextPath}/quartz/kit/task/add">添加定时任务</a>
        </div>
    </form>
    <hr>
    <#-- 垂直对齐 -->
    <div class="uk-overflow-auto">
        <table class="uk-table uk-table-middle uk-table-divider uk-table-striped">
            <thead>
            <tr>
                <th>任务类</th>
                <th>任务分组</th>
                <th>任务描述</th>
                <th>任务表达式</th>
                <th>任务状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <#list tasks as task>
                <tr>
                    <td>${task.taskClass}</td>
                    <td>${task.taskGroup}</td>
                    <td>${task.taskDescription}</td>
                    <td>${task.taskExpression}</td>
                    <td>
                        <#if task.taskStatus == 1>
                            <button class="uk-button uk-button-primary uk-button-small" onclick="void(0)">ON</button>
                        </#if>
                        <#if task.taskStatus == 0>
                            <button class="uk-button uk-button-danger uk-button-small" onclick="void(0)">OFF</button>
                        </#if>
                    </td>
                    <td>
                        <button class="uk-button uk-button-default uk-button-small btn-trigger" type="button"
                                data-xid="${task.taskId}">
                            触发
                        </button>
                        <#if task.taskStatus == 1>
                            <button class="uk-button uk-button-default uk-button-small btn-stop" type="button"
                                    data-xid="${task.taskId}">
                                禁用
                            </button>
                        <#elseif task.taskStatus == 0>
                            <button class="uk-button uk-button-default uk-button-small btn-start" type="button"
                                    data-xid="${task.taskId}">
                                启用
                            </button>
                        <#else>
                        </#if>
                        <a class="uk-button uk-button-default uk-button-small"
                           href="${request.contextPath}/quartz/kit/task/edit/${task.taskId}">
                            编辑
                        </a>
                        <button class="uk-button uk-button-danger uk-button-small btn-delete" type="button"
                                data-xid="${task.taskId}">
                            删除
                        </button>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>
<script type="text/javascript">
    $('.btn-delete').on('click', function () {
        let btn = $(this);
        let taskId = btn.data('xid');
        let url = '${request.contextPath}/quartz/kit/task/delete/' + taskId;
        $.get(url, function (response) {
            if (response && response.code === 200) {
                UIkit.notification({
                    message: '<span uk-icon=\'icon: check\'></span> 删除定时任务' + taskId + '成功!',
                    status: 'success'
                });
                window.location.reload();
            } else {
                UIkit.notification({
                    message: '删除定时任务' + taskId + '失败!',
                    status: 'danger'
                });
            }
        });
    });

    $('.btn-trigger').on('click', function () {
        let btn = $(this);
        let taskId = btn.data('xid');
        let url = '${request.contextPath}/quartz/kit/task/trigger/' + taskId;
        $.get(url, function (response) {
            if (response && response.code === 200) {
                UIkit.notification({
                    message: '<span uk-icon=\'icon: check\'></span> 触发定时任务' + taskId + '成功!',
                    status: 'success'
                });
                window.location.reload();
            } else {
                UIkit.notification({message: '触发定时任务' + taskId + '失败!', status: 'danger'});
            }
        });
    });

    $('.btn-stop').on('click', function () {
        let btn = $(this);
        let taskId = btn.data('xid');
        let url = '${request.contextPath}/quartz/kit/task/stop/' + taskId;
        $.get(url, function (response) {
            if (response && response.code === 200) {
                UIkit.notification({
                    message: '<span uk-icon=\'icon: check\'></span> 禁用定时任务' + taskId + '成功!',
                    status: 'success'
                });
                window.location.reload();
            } else {
                UIkit.notification({message: '禁用定时任务' + taskId + '失败!', status: 'danger'});
            }
        });
    });

    $('.btn-start').on('click', function () {
        let btn = $(this);
        let taskId = btn.data('xid');
        let url = '${request.contextPath}/quartz/kit/task/start/' + taskId;
        $.get(url, function (response) {
            if (response && response.code === 200) {
                UIkit.notification({
                    message: '<span uk-icon=\'icon: check\'></span> 启动定时任务' + taskId + '成功!',
                    status: 'success'
                });
                window.location.reload();
            } else {
                UIkit.notification({
                    message: '启动定时任务' + taskId + '失败!',
                    status: 'danger'
                });
            }
        });
    });
</script>
</body>
</html>