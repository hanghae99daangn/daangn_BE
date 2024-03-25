package com.sparta.market.domain.chat.mapper;


import com.sparta.market.domain.chat.dto.ChatDto;
import com.sparta.market.domain.chat.entity.ChatMessage;
import com.sparta.market.domain.chat.entity.ChatRoom;
import com.sparta.market.domain.user.dto.UserDto;
import com.sparta.market.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    User ChatPostDtoToMember(ChatDto.Post post);
    @Mapping(source = "UserProfile.id", target = "UserProfile.id")
    @Mapping(source = "UserProfile.imageName", target = "UserProfile.imageName")
    @Mapping(source = "UserProfile.url", target = "UserProfile.url")
    UserDto.ResponseOnlyUserName memberToUserNameResponseDto(User user);

    ChatDto.RoomResponse chatRoomToRoomResponseDto(ChatRoom chatRoom);
    List<ChatDto.RoomResponse> chatRoomListToRoomResponseDtos(List<ChatRoom> chatRooms);

    ChatDto.MessageResponse messageToMessageResponseDto(ChatMessage message);

    List<ChatDto.MessageResponse> messagesToMessageResponseDtos(List<ChatMessage> messages);

}
