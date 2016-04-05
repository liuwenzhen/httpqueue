package org.httpqueue.outprocess.task;

import org.apache.log4j.Logger;
import org.httpqueue.outprocess.task.intf.IReadMemory;
import org.httpqueue.protocolbean.MessageBody;
import org.httpqueue.protocolbean.Mode;
import org.httpqueue.util.CommonConst;
import org.httpqueue.util.redis.RedisShard;
import redis.clients.jedis.ShardedJedis;

/**
 * Created by andilyliao on 16-3-31.
 */
public class ReadMemory implements IReadMemory {
    private static Logger log = Logger.getLogger(ReadMemory.class);
    @Override
    public void registDirectQueue(String queueName) throws Exception {
        //TODO 目前没有任务，后面需要做每个队列的注册消费者数量的监控
    }

    @Override
    public int registFanoutQueue(String clientID,String queueName) throws Exception {
        //TODO 后面需要做每个队列的注册消费者数量的监控
        ShardedJedis jedis = RedisShard.getJedisObject();
        String pubset="0";
        try {
            pubset = jedis.get(queueName + CommonConst.splitor + CommonConst.PUBSET);
            jedis.set(queueName + CommonConst.splitor + CommonConst.OFFSET + CommonConst.splitor + clientID, "0");
        }catch(Exception e){
            log.error("system error:",e);
        }finally {
            RedisShard.returnJedisObject(jedis);
        }
        return Integer.parseInt(pubset);
    }

    @Override
    public int registTopic(String clientID,String queueName) throws Exception {
        //TODO 后面需要做每个队列的注册消费者数量的监控
        ShardedJedis jedis=RedisShard.getJedisObject();
        String pubset="0";
        try {
            pubset =jedis.get(queueName+ CommonConst.splitor+CommonConst.PUBSET);
            jedis.set(queueName+ CommonConst.splitor+CommonConst.OFFSET+CommonConst.splitor+clientID,pubset);
        }catch(Exception e){
            log.error("system error:",e);
        }finally {
            RedisShard.returnJedisObject(jedis);
        }
        return Integer.parseInt(pubset);
    }

    @Override
    public MessageBody outputDirect(String queName, int offset, int seq) throws Exception {
        ShardedJedis jedis=RedisShard.getJedisObject();
        if(!jedis.exists(queName)){
            RedisShard.returnJedisObject(jedis);
            throw new Exception("This queue isn't exsit,please check! queueName is: "+queName);
        }
        int type=Integer.parseInt(jedis.hget(queName, CommonConst.TYPE));
        if(type!=Mode.MODE_DIRECT){
            RedisShard.returnJedisObject(jedis);
            throw new Exception("This queue isn't a direct queue,please check! queueName is: "+queName);
        }
        long reoffset=0;
        long putset=0;
        String body="";
        String[] bodyseqandtotle;
        int getseq=0;
        int gettotleseq=0;
        try {
            String key=queName+CommonConst.splitor+CommonConst.puboffsetAndSeq(offset,seq);
            log.debug("key: "+key+" jedis: "+jedis);
            bodyseqandtotle=jedis.get(key).split(CommonConst.splitor);
            getseq=Integer.parseInt(bodyseqandtotle[1]);
            gettotleseq=Integer.parseInt(bodyseqandtotle[2]);
            body=bodyseqandtotle[0];
            log.debug("body: "+body+" getseq: "+getseq+" gettotoleseq: "+gettotleseq);
            reoffset=jedis.incr(queName+ CommonConst.splitor+CommonConst.OFFSET);
            putset=Long.parseLong(jedis.get(queName + CommonConst.splitor + CommonConst.PUBSET));
        }catch(Exception e){
            log.error("system error:",e);
        }finally {
            RedisShard.returnJedisObject(jedis);
        }
        return new MessageBody(putset,reoffset,body,getseq,gettotleseq);
    }

    @Override
    public MessageBody outputFanout(String clientID, String queName, int offset, int seq) throws Exception {
        ShardedJedis jedis=RedisShard.getJedisObject();
        if(!jedis.exists(queName)){
            RedisShard.returnJedisObject(jedis);
            throw new Exception("This queue isn't exsit,please check! queueName is: "+queName);
        }
        int type=Integer.parseInt(jedis.hget(queName, CommonConst.TYPE));
        if(type!=Mode.MODE_FANOUT){
            RedisShard.returnJedisObject(jedis);
            throw new Exception("This queue isn't a direct queue,please check! queueName is: "+queName);
        }
        long reoffset=0;
        long putset=0;
        String body="";
        String[] bodyseqandtotle;
        int getseq=0;
        int gettotleseq=0;
        try {
            String key=queName+ CommonConst.splitor+CommonConst.puboffsetAndSeq(offset,seq);
            bodyseqandtotle=jedis.get(key).split(CommonConst.splitor);
            getseq=Integer.parseInt(bodyseqandtotle[1]);
            gettotleseq=Integer.parseInt(bodyseqandtotle[2]);
            body=bodyseqandtotle[0];
            reoffset=jedis.incr(queName+ CommonConst.splitor+CommonConst.OFFSET+CommonConst.splitor+clientID);
            putset=Long.parseLong(jedis.get(queName + CommonConst.splitor + CommonConst.PUBSET));
        }catch(Exception e){
            log.error("system error:",e);
        }finally {
            RedisShard.returnJedisObject(jedis);
        }
        return new MessageBody(putset,reoffset,body,getseq,gettotleseq);
    }

    @Override
    public MessageBody outputTopic(String clientID, String queName, int offset, int seq) throws Exception {
        ShardedJedis jedis=RedisShard.getJedisObject();
        if(!jedis.exists(queName)){
            RedisShard.returnJedisObject(jedis);
            throw new Exception("This queue isn't exsit,please check! queueName is: "+queName);
        }
        int type=Integer.parseInt(jedis.hget(queName, CommonConst.TYPE));
        if(type!=Mode.MODE_TOPIC){
            RedisShard.returnJedisObject(jedis);
            throw new Exception("This queue isn't a direct queue,please check! queueName is: "+queName);
        }
        long reoffset=0;
        long putset=0;
        String body="";
        String[] bodyseqandtotle;
        int getseq=0;
        int gettotleseq=0;
        try {
            String key=queName+ CommonConst.splitor+CommonConst.puboffsetAndSeq(offset,seq);
            bodyseqandtotle=jedis.get(key).split(CommonConst.splitor);
            getseq=Integer.parseInt(bodyseqandtotle[1]);
            gettotleseq=Integer.parseInt(bodyseqandtotle[2]);
            body=bodyseqandtotle[0];
            reoffset=jedis.incr(queName+ CommonConst.splitor+CommonConst.OFFSET+CommonConst.splitor+clientID);
            putset=Long.parseLong(jedis.get(queName + CommonConst.splitor + CommonConst.PUBSET));
        }catch(Exception e){
            log.error("system error:",e);
        }finally {
            RedisShard.returnJedisObject(jedis);
        }
        return new MessageBody(putset,reoffset,body,getseq,gettotleseq);
    }

    @Override
    public int getQueMode(String queName) throws Exception {
        ShardedJedis jedis=RedisShard.getJedisObject();
        try{
            return Integer.parseInt(jedis.hget(queName, CommonConst.TYPE));
        }catch(Exception e){
            log.error("system error:",e);
        }finally {
            RedisShard.returnJedisObject(jedis);
        }
        throw new Exception("system error:");
    }
}
