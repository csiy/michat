package com.chester.michat.server;

import com.chester.michat.model.Message;
import com.chester.michat.model.Receive;
import com.chester.michat.utils.RSAUtils;
import com.chester.michat.utils.ZipUtils;
import com.chester.michat.utils.json.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.DatagramPacket;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.udp.UdpInbound;
import reactor.netty.udp.UdpOutbound;
import reactor.netty.udp.UdpServer;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Configuration
public class ChatServer {

    private final Map<String, InetSocketAddress> addressMap = new HashMap<>();
    private final Map<String, String> p_map = new HashMap<>();
    private final Map<InetSocketAddress, String> uMap = new HashMap<>();

    String uuid() {
        UUID id = UUID.randomUUID();
        return id.toString().replaceAll("-", "");
    }

    @Bean
    public BiFunction<? super UdpInbound, ? super UdpOutbound, ? extends Publisher<Void>> biFunction() {
        return (in, out) ->
                out.sendObject(
                        in.receiveObject().map(o -> {
                            try {
                                if (o instanceof DatagramPacket) {
                                    DatagramPacket packet = (DatagramPacket) o;
                                    InetSocketAddress sender = packet.sender();
                                    if (uMap.get(sender) == null) {
                                        String u = uuid();
                                        uMap.put(sender, u);
                                        addressMap.put(u, sender);
                                    }
                                    ByteBuf byteBuf = packet.content();
                                    byte[] data = new byte[byteBuf.readableBytes()];
                                    byteBuf.readBytes(data);
                                    Receive decrypt = decrypt(data);
                                    p_map.put(decrypt.getU(), decrypt.getP());
                                    InetSocketAddress _sender = addressMap.get(decrypt.getU());
                                    Message message = new Message(sender.getPort(), sender.getHostName(), p_map.get(decrypt.getU()));
                                    out.sendObject(new DatagramPacket(Unpooled.copiedBuffer(encrypt(message)), _sender));
                                    Message _message = new Message(sender.getPort(), sender.getHostName(), p_map.get(uMap.get(sender)));
                                    return new DatagramPacket(Unpooled.copiedBuffer(encrypt(_message)), packet.sender());
                                } else {
                                    return Mono.error(new Exception("Unexpected type of the message: " + o));
                                }
                            } catch (Exception e) {
                                return Mono.error(e);
                            }
                        })
                );
    }

    private byte[] encrypt(Message message) throws Exception {
        byte[] zip = ZipUtils.zip(JSON.serialize(message));
        return RSAUtils.encrypt(zip, message.getP());
    }

    private Receive decrypt(byte[] data) throws Exception {
        byte[] decrypt = RSAUtils.decrypt(data, RSAUtils.getPrivateKey());
        byte[] unzip = ZipUtils.unzip(decrypt);
        return JSON.parse(unzip, Receive.class);
    }

    @Bean
    public Connection createdUdpChatServer() {
        return UdpServer.create()
                .handle(biFunction())
                .option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_RCVBUF, 10000 * 1024)
                .option(ChannelOption.SO_SNDBUF, 10000 * 1024)
                .port(9527)
                .bindNow(Duration.ofSeconds(30));
    }
}
