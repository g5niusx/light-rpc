package com.g5niusx.rpc.serialization;

/**
 * 序列化和反序列化接口
 *
 * @author g5niusx
 */
public interface RpcSerializationService {
    /**
     * 序列化
     *
     * @param t 对象实例
     * @return 字节数组
     */
    <T> byte[] serialize(T t);

    /**
     * 反序列化
     *
     * @param bytes 序列化以后的字符串
     * @return 对象实例
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
