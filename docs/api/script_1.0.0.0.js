var my_autoReport = false;
var my_temp="36.6";
var my_autologin_username=null;
var my_autologin_password=null;
var my_reportUrl="https://hsm.sspu.edu.cn/selfreport/DayReport.aspx";
var my_historyUrl="https://hsm.sspu.edu.cn/selfreport/ReportHistory.aspx";
var my_targetUrl=my_reportUrl;

function myEnableAuto(){
    my_autoReport=true;
    console.log("auto report is on.");
}
function myOnLoadEnd(url){
    ProgressAccessor.hideProgress();
    if(url.startsWith("https://id.sspu.edu.cn/cas/login")){
        ProgressAccessor.showProgress("一键登录中");
        console.log("url is login address, inputing passwords.");
        document.getElementById("username").value=my_autologin_username;
        document.getElementById("password").value=my_autologin_password;
        document.getElementsByClassName("submit_button")[0].click();
    }
    if(url.startsWith("https://hsm.sspu.edu.cn/selfreport/Default.aspx")){
        ProgressAccessor.showProgress("跳转中");
        console.log("url is homepage, going to target url");
        window.location.href=my_targetUrl;
    }

    if(url.startsWith(my_reportUrl)){
        if(my_autoReport == true){
            console.log("autoreport is on, now begin report process");
            setTimeout(function(){
                ProgressAccessor.fail("一键填报暂未开通");
            },1000);
            ProgressAccessor.showProgress("一键填报中");
        }
        else{
            myQuitCheck = setInterval(myAutoQuit,800);
        }
    }
}

var myQuitCheck = null;
function myAutoQuit(){
    console.log("detecting success message...");
    if(document.getElementsByClassName("f-messagebox-message")[0].innerText.indexOf("提交成功") != -1){
        console.log("success message detected.");
        clearInterval(myQuitCheck);
        console.log("remove timer.");
        ProgressAccessor.hideProgress();
        ProgressAccessor.success();
        console.log("Call onSuccess()");
    }
}
function mySetHistoryMode(){
    my_targetUrl=my_historyUrl;
    console.log("mode set to viewhistory");
}

function mySetUser(username,password){
    console.log("setting username");
    my_autologin_username=username;
    my_autologin_password=password;
}
function mySetTemperature(temp){
    console.log("setting temperature");
    my_temp=temp;
}

console.log("Operator injection success.");
