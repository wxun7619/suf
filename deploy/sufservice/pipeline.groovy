//def comm
///* 标签 */
//def gittag
///* 标签名称 */
//def tagtype
///* 脚本项目路径 */
//def spath="./../$JOB_BASE_NAME@script"
///* 脚本路径 */
//def groovypath="$spath/deploy"
//
///* Jenkins - 代码库地址 */
//def giturl="ssh://git@gitsvr.mipesoft.com:1022/scmteam/domainmanager-service.git"
//
///* BuildServer - 镜像构建服务器IP */
//def imageserver="192.168.212.13"
///* BuildServer - 部署文件所在的远程目录 */
//def remotedir="/dockerbuild/sufservice"
///* BuildServer - 镜像底包地址 */
//def fromimage="java:8-jre-alpine"
//
///* Kubernetes - master主节点IP */
//def k8smaster="192.168.212.13"
///* Kubernetes - 命名空间 */
//def namespace="domainservice-uat"
///* Kubernetes - pull镜像时所用到的密钥 */
//def secretkey="aliyuncs-secret"
///* Kubernetes - 应用名称 */
//def appname="sufservice-app"
///* Kubernetes - 服务名称 */
//def svcname="sufservice-srv"
///* Kubernetes - 对外暴露的服务端口 */
//def svcport="9106"
///* Kubernetes - 公共对外暴露的服务端口 */
//def nodeport="5087"
///* Kubernetes - push/pull镜像目录地址 */
//def imageurl="registry-vpc.cn-shenzhen.aliyuncs.com/labelcloud/scm_sufservice"
///* git brancheName */
//def brancheName="develop"
///* jar package path */
//def jarPath="sufservice/build/libs/sufservice-1.0.0.jar"
///* module name */
//def moduleName="sufservice"
//
//stage '签出代码'
//node('master') {
//    sh 'echo `pwd`'
//    //sh "sed -i 's/\\r//g' $spath/*.*"
//    comm = load "$groovypath/common.groovy"
//    comm.gitCheckout("$brancheName","gitlab-jenkins","$giturl")
//    gittag = comm.getGitTagName()
//}
//
//// stage '替换配置文件'
//// node('master') {
////     sh 'echo `pwd`'
////     sh 'cat deploy/config/application.properties_release > webapi/src/main/resources/application.properties'
//// }
//
//stage '构建JAR包'
//node() {
//    env.JAVA_HOME="$JAVA_HOME_18"
//    bat "chcp 65001\r\ncd $moduleName\r\n$GRADLE470 clean build -x test"
//}
//
//stage '准备部署文件'
//node('master') {
//    sh 'echo `pwd`'
//
//    sh "sed -i 's/\\r//g' $groovypath/$moduleName/start.sh"
//    sh "ssh root@$imageserver mkdir -p $remotedir/"
//    sh "scp -r $jarPath root@$imageserver:$remotedir/"
//    sh "scp -r $groovypath/$moduleName/start.sh root@$imageserver:$remotedir/"
//}
//
//stage 'Build DockerImage'
//node('master') {
//    currentBuild.result = 'SUCCESS'
//    try {
//        sh "sed -i 's/\\r//g' $groovypath/build-docker.sh"
//        sh "scp $groovypath/build-docker.sh root@$imageserver:~"
//        sh "ssh root@$imageserver chmod a+x ./build-docker.sh"
//
//        sh "ssh root@$imageserver ./build-docker.sh \
//            --from=$fromimage \
//            --tagname=$imageurl \
//            --tagver=$gittag \
//            --sourcedir=$remotedir/ \
//            --imagedir=/var/$moduleName \
//            --expose=$svcport \
//            --startparam=/var/$moduleName/start.sh"
//        sh "ssh root@$imageserver rm -rf ./build-docker.sh"
//    }
//    catch(err) {
//        currentBuild.result = 'FAILURE'
//        throw err
//    }
//}
//
//stage '内网k8s部署'
//node('master') {
//    currentBuild.result = 'SUCCESS'
//    try {
//        sh "sed -i 's/\\r//g' $groovypath/deploy.sh"
//        sh "scp $groovypath/deploy.sh root@$k8smaster:~;ssh root@$k8smaster chmod a+x ./deploy.sh"
//
//        sh "ssh root@$k8smaster ./deploy.sh \
//            --appname=$appname \
//            --image=$imageurl \
//            --version=$gittag \
//            --namespace=$namespace \
//            --replicas=1 \
//            --secretkey=$secretkey \
//            --servicename=$svcname \
//            --serviceport=$svcport \
//            --nodeport=$nodeport \
//	        --memorylimit=200Mi,400Mi \
//	        --cpulimit=100m,750m"
//    }
//    catch(err) {
//        currentBuild.result = 'FAILURE'
//        throw err
//    }
//}
