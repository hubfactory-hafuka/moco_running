<!DOCTYPE html>

<html lang="en">

<body>
    <#list girls as girl>
        ${girl.girlId!?html}<br>
        ${girl.type!?html}<br>
        ${girl.name!?html}<br>
        ${girl.age!?html}<br>
        ${girl.height!?html}<br>
        ${girl.weight!?html}<br>
        ${girl.hobby!?html}<br>
        ${girl.profile!?html}<br>
        ${girl.cv!?html}<br>
        ${girl.price!?html}<br>
        ${girl.startDatetime!?html}<br>
        ${girl.endDatetime!?html}<br>
    </#list>
</body>

</html>