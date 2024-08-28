<p>
Sample rar based on ironjacamar implementation of JCA 1.6. It supports both <b>LocalTransactions</b> and <b>XATransactions</b>.
There are three testsuites under testsuite/integration/smoke, first test will verify that rar is deployed and undeployed successfully, second one will verify transaction commit usecase, third one will verify rollback usecase.
</p>
<p>
To build run: mvn clean install
</p>
<p>
To run integration tests: mvn clean install -DintegrationTests (this would run tests against EAP 6.1.1) alternatively you can specify old version of EAP with -Deap.version=6.0.1
</p>
<p>
<b>mvn clean install -DintegrationTests or mvn clean install -DintegrationTests -Deap.version=6.1.1</b>
</p>
or
<p>
<b>mvn clean install -DintegrationTests -Deap.version=6.0.1</b></br>
</p>
<p>
Code is preconfigured to use LocalTransactions, this can be changed in file-connector-ra/src/main/rar/META-INF/ironjacamar.xml
change <transaction-support>LocalTransaction</transaction-support> to <transaction-support>XATransaction</transaction-support> if required</p> 
<p>
Log will contain lines like this, depending which EAP is being used: (for example testsuite/integration/smoke/target/jboss-as-dist-jboss-eap-6.0.1/standalone/log/server.log) :
</p>
<p>
<b>16:13:30,548 TRACE [com.ericsson.nms.security.aicore.ra.FileManagedConnection] (http-localhost/127.0.0.1:8580-2) commit called with xid=[XidWrapperImpl@4b4f4602[formatId=131077 globalTransactionId=...</b>
</p>
or
<p>
<b>16:13:27,111 TRACE [com.ericsson.nms.security.aicore.ra.FileManagedConnection] (http-localhost/127.0.0.1:8580-2) rollback called for xid=[XidWrapperImpl@706e715c[formatId=131077 globalTransactionId=...</b>
</p>

