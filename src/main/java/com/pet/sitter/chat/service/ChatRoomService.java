package com.pet.sitter.chat.service;

import com.pet.sitter.chat.controller.ChatMessageController;
import com.pet.sitter.chat.dto.ChatRoomDTO;
import com.pet.sitter.chat.repository.ChatRoomRepository;
import com.pet.sitter.common.entity.ChatRoom;
import com.pet.sitter.mainboard.repository.PetsitterRepository;
import com.pet.sitter.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChatRoomService {

    @Autowired
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    private PetsitterRepository petsitterRepository;

    @Autowired
    ChatMessageService chatMessageService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    public ChatRoomDTO createChatRoom(Long id, String hostId, String guestId) {
        System.out.println("PetSitterId(Service): " + id);
        ChatRoom chatRoom = new ChatRoom();

        chatRoom.setPetsitter(petsitterRepository.findBySitterNo(id));
        chatRoom.setRoomUUID(UUID.randomUUID().toString());
        chatRoom.setName(chatRoom.getPetsitter().getPetTitle() + " [" +
                memberRepository.findMemberByMemberId(hostId).getNickname() + " && " +
                memberRepository.findMemberByMemberId(guestId).getNickname() + "]");
        chatRoom.setCreateDate(LocalDateTime.now());
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        chatMessageService.enterMessage(chatRoom.getId(), hostId);
        chatMessageService.enterMessage(chatRoom.getId(), guestId);

        return convertToChatRoomDTO(savedChatRoom);
    }

    public ChatRoom getChatRoomById(Long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    public ChatRoom getChatRoomByRoomUUID(String roomUUID) {
        return chatRoomRepository.findChatRoomByRoomUUID(roomUUID);
    }

    private ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom) {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setId(chatRoom.getId());
        chatRoomDTO.setRoomUUID(chatRoom.getRoomUUID());
        chatRoomDTO.setCreateDate(chatRoom.getCreateDate());
        System.out.println("chatRoomDTO_ID: " + chatRoomDTO.getId());
        return chatRoomDTO;
    }

    public String getChatRoomUUIDById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not Found"));
        return chatRoom.getRoomUUID();
    }
}