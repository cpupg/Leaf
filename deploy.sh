PROJECT_DIR=$(pwd);
DEPLOY_DIR=$PROJECT_DIR/deploy;
JAR_DIR=$PROJECT_DIR/leaf-server/target;
LIB_DIR=$DEPLOY_DIR/lib;
SHELL_FILE=$DEPLOY_DIR/start-server.sh;

echo "项目目录:$PROJECT_DIR";

echo "开始打包";

echo "删除旧包";
rm -fv $DEPLOY_DIR/*.jar;

if [ ! -e ${DEPLOY_DIR} ];then
  echo "部署目录不存在，创建目录:$DEPLOY_DIR";
  mkdir $DEPLOY_DIR;
fi

mvn -DskipTests=true clean package;
mv -vf $JAR_DIR/*.jar $DEPLOY_DIR;
jar_name="$(ls $DEPLOY_DIR/*.jar)";

echo "打包完成，包名${jar_name}，开始复制依赖";
if [ -e $LIB_DIR ]; then
  echo "依赖目录${LIB_DIR}存在，不需要再次复制依赖";
else
  mkdir $LIB_DIR;
  cd leaf-server;
  mvn dependency:copy-dependencies;
  mv -fv target/dependency/* $LIB_DIR;
  echo "依赖复制完成";
  cd $PROJECT_DIR;
fi

echo "开始生成启动脚本";
cd $DEPLOY_DIR;
jar_name="$(ls $DEPLOY_DIR/*.jar)";
echo "java -cp $jar_name com.sankuai.inf.leaf.server.LeafServerApplication %*">$SHELL_FILE;
echo "启动脚本生成完成";
