def comm
/* 标签 */
def gittag
/* 标签名称 */
def tagtype
/* 脚本项目路径 */
def spath="./../$JOB_BASE_NAME@script"
/* 脚本路径 */
def groovypath="$spath/deploy"

/* Jenkins - 代码库地址 */
def giturl="ssh://git@gitsvr.mipesoft.com:1022/scmteam/domainmanager-service.git"
def moduleNames = ['usersystem', 'domainservice', 'sufservice', 'domainservice.adminportal', 'coderuleservice']
def appNames = ['usersystem', 'domainservice', 'sufservice', 'dsadminportal', 'coderuleservice']
def imageurls = [
        'registry-vpc.cn-shenzhen.aliyuncs.com/labelcloud/scm_usersystem',
        'registry-vpc.cn-shenzhen.aliyuncs.com/labelcloud/scm_domainservice',
        'registry-vpc.cn-shenzhen.aliyuncs.com/labelcloud/scm_sufservice',
        'registry-vpc.cn-shenzhen.aliyuncs.com/labelcloud/scm_dsadminportal',
        'registry-vpc.cn-shenzhen.aliyuncs.com/labelcloud/scm_coderuleservice'
]
/* 应用程序 - 服务进程端口 */
def svcports =['9105','9107','9106','9109', '9110']
/* Kubernetes - 公共对外暴露的服务端口 */
def nodeports =['5082','5085','5087','5086', '5088']
/* BuildServer - 镜像构建服务器IP */
//def imageserver="192.168.212.13"
def imageserver="192.168.211.101"
/* Kubernetes - master主节点IP */
//def k8smaster="192.168.212.13"
def k8smaster="192.168.211.101"
/* BuildServer - 镜像底包地址 */
def fromimage="registry-vpc.cn-shenzhen.aliyuncs.com/ccw-registry/java:8u111-jre-env"
/* Kubernetes - 命名空间 */
def namespace="domainservice-uat"
/* Kubernetes - pull镜像时所用到的密钥 */
def secretkey="aliyuncs-secret"
/* git brancheName */
def brancheName="develop"


stage '签出代码'
node('master') {
    sh 'echo `pwd`'
    //sh "sed -i 's/\\r//g' $spath/*.*"
    comm = load "$groovypath/common.groovy"
    comm.gitCheckout("$brancheName","gitlab-jenkins","$giturl")
    gittag = comm.getGitTagName()
}

// stage '替换配置文件'
// node('master') {
//     sh 'echo `pwd`'
//     sh 'cat deploy/config/application.properties_release > webapi/src/main/resources/application.properties'
// }

stage '构建JAR包'
node() {
    env.JAVA_HOME="$JAVA_HOME_18"
    bat "chcp 65001\r\n$GRADLE470 clean build -x test"
}

stage '准备部署文件'
node('master') {
    sh 'echo `pwd`'

    for(int i=0; i<moduleNames.size(); i++) {
        def moduleName = moduleNames[i]
        def jarPath = "$moduleName/build/libs/$moduleName-1.0.0.jar"
        def remotedir="/dockerbuild/$moduleName"

        sh "sed -i 's/\\r//g' $groovypath/$moduleName/start.sh"
        sh "ssh root@$imageserver mkdir -p $remotedir/"
        sh "scp -r $jarPath root@$imageserver:$remotedir/"
        sh "scp -r $groovypath/$moduleName/start.sh root@$imageserver:$remotedir/"
    }
}

stage 'Build DockerImage'
node('master') {
    currentBuild.result = 'SUCCESS'
    try {
        for(int i=0;i<moduleNames.size(); i++){
            sh "sed -i 's/\\r//g' $groovypath/build-docker.sh"
            sh "scp $groovypath/build-docker.sh root@$imageserver:~"
            sh "ssh root@$imageserver chmod a+x ./build-docker.sh"

            def moduleName = moduleNames[i]
            def imageurl = imageurls[i]
            def remotedir="/dockerbuild/$moduleName"
            def svcport = svcports[i]

            sh "ssh root@$imageserver ./build-docker.sh --from=$fromimage --tagname=$imageurl --tagver=$gittag --sourcedir=$remotedir/ --imagedir=/var/$moduleName --expose=$svcport --startparam=/var/$moduleName/start.sh"
            sh "ssh root@$imageserver rm -rf ./build-docker.sh"
        }
    }
    catch(err) {
        currentBuild.result = 'FAILURE'
        throw err  
    }
}

stage '内网k8s部署'
node('master') {
    currentBuild.result = 'SUCCESS'
    try {
        sh "sed -i 's/\\r//g' $groovypath/deploy.sh"
        sh "scp $groovypath/deploy.sh root@$k8smaster:~;ssh root@$k8smaster chmod a+x ./deploy.sh"

        for(int i=0;i<moduleNames.size(); i++) {

            def baseName = appNames[i]
            def appname = "$baseName-app"
            def svcname = "$baseName-srv"
            def imageurl = imageurls[i]
            def svcport = svcports[i]
            def nodeport = nodeports[i]

            sh "ssh root@$k8smaster ./deploy.sh --appname=$appname --image=$imageurl --version=$gittag --namespace=$namespace --replicas=1 --secretkey=$secretkey --servicename=$svcname --serviceport=$svcport --nodeport=$nodeport --memorylimit=200Mi,400Mi --cpulimit=100m,750m"
        }
    }
    catch(err) {
        currentBuild.result = 'FAILURE'
        throw err  
    }
}
