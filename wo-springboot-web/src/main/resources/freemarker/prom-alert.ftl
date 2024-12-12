<#compress>
    <#list alerts! as alert>
        告警客户: ${alert.labels["customer"]}
        告警类型: ${alert.labels["alertname"]}
        告警状态: ${alert.status}
        告警级别: ${alert.labels["severity"]}
        告警应用: ${alert.labels["application"]}
        告警主机: ${alert.labels["instance"]}
        告警时间: ${alert.startsAt}
        告警信息: ${alert.annotations["summary"]}
        <#if alert?has_next>=== === ===</#if>
    </#list>
</#compress>