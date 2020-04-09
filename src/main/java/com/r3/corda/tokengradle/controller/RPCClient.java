package com.r3.corda.tokengradle.controller;


import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.node.NodeInfo;
import net.corda.core.utilities.NetworkHostAndPort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.*;
import com.template.states.AccountState;
import com.template.schema.AccountSchemaV1;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.DataFeed;
import rx.Observable;
import net.corda.core.node.services.VaultService;
import net.corda.core.node.services.*;
//import net.corda.node.services.vault.*;
import net.corda.core.node.services.vault.FieldInfo;
//import net.corda.core.node.services.vault.*;
import net.corda.core.node.services.vault.QueryCriteria.*;
import static net.corda.core.node.services.vault.QueryCriteriaUtils.*;
//import net.corda.core.node.services.vault.FieldInfo;
//import net.corda.core.node.services.vault.QueryCriteria.VaultCustomQueryCriteria;
//import tokengradle.libs.contracts-0.1.jar;
//import net.corda.core.node.vault.Builder.equal;
//import net.corda.core.node.vault.QueryCriteria;
//import net.corda.core.node.vault.builder;
import java.util.List;

//@RestController
//public class RPCClient {
//
//    @RequestMapping(value = "/getNetworkMapSnapshot", method = RequestMethod.GET)
//    public List<NodeInfo> getNetworkMapSnapshot(){
//        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse("localhost:10006");
//        String username = "user1";
//        String password = "test";
//
//        final CordaRPCClient client = new CordaRPCClient(nodeAddress);
//        final CordaRPCConnection connection = client.start(username, password);
//        final CordaRPCOps proxy = connection.getProxy();
//
//        final List<NodeInfo> nodes = proxy.networkMapSnapshot();
//
//        return nodes;
//    }
//
////    @RequestMapping(value = "/vaultQuery", method = RequestMethod.GET)
////    public List<AccountState> vaultQuery(){
////        List<stateAndRef<AccountState>> vault =proxy.vaultQuery(AccountState::class.java).states;
////
////        return vault.map{it.state.data};
////    }
//
//
//    @RequestMapping(value = "/hello", method = RequestMethod.GET)
//    public String hello(){
//        return "Hello";
//    }
//}
@RestController
public class RPCClient {

    @RequestMapping(value = "/getNetworkMapSnapshot", method = RequestMethod.GET)
    public List<NodeInfo> getNetworkMapSnapshot(){

        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse("localhost:10006");
        String username = "user1";
        String password = "test";

        final CordaRPCClient client = new CordaRPCClient(nodeAddress);
        final CordaRPCConnection connection = client.start(username, password);
        final CordaRPCOps proxy = connection.getProxy();

        final List<NodeInfo> nodes = proxy.networkMapSnapshot();

        return nodes;
    }

    @RequestMapping(value ="/vaultQuery/{accountid}", method=RequestMethod.GET)
    public List<StateAndRef<AccountState>> vaultQuery(@PathVariable("accountid") String accountid) throws Exception{
        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse("localhost:10006");
        String username = "user1";
        String password = "test";

        final CordaRPCClient client = new CordaRPCClient(nodeAddress);
        final CordaRPCConnection connection = client.start(username, password);
        final CordaRPCOps proxy = connection.getProxy();

        QueryCriteria generalCriteria = new VaultQueryCriteria(Vault.StateStatus.ALL);
//        val field = entityClass.declaredField<Any>(AccountSchemaV1.PersistentAccount.class, "accountId")

            FieldInfo attributeAccount = QueryCriteriaUtils.getField("accountId", AccountSchemaV1.PersistentAccount.class);


            CriteriaExpression accountIdCriteria = Builder.equal(attributeAccount, accountid);
            QueryCriteria customCriteria = new VaultCustomQueryCriteria(accountIdCriteria);
            QueryCriteria criteria = generalCriteria.and(customCriteria);
            //hit the node to get snapshot and observable for IOUState
            DataFeed<Vault.Page<AccountState>, Vault.Update<AccountState>> dataFeed = proxy.vaultTrackByCriteria(AccountState.class, criteria);

            //this gives a snapshot of IOUState as of now. so if there are 11 IOUState as of now, this will return 11 IOUState objects
            Vault.Page<AccountState> snapshot = dataFeed.getSnapshot();

            //this returns an observable on IOUState
//        Observable<Vault.Update<AccountState>> updates = dataFeed.getUpdates();

            // call a method for each IOUState
            return snapshot.getStates();



    }

//    @GetMapping(value = "/states", produces = arrayOf("text/plain"))
//    private fun states() = proxy.vaultQueryBy<AccountState>().states.toString();

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello(){
        return "Hello";
    }
}