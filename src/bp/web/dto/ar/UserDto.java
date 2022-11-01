package bp.web.dto.ar;

import eulap.eb.domain.hibernate.User;

public class UserDto extends User{
	public static UserDto getInstanceOf (User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		userDto.setPassword(user.getPassword());
		userDto.setFirstName(user.getFirstName());
		userDto.setLastName(user.getLastName());
		userDto.setMiddleName(user.getMiddleName());
		userDto.setAddress(user.getAddress());
		userDto.setBirthDate(user.getBirthDate());
		userDto.setContactNumber(user.getContactNumber());
		userDto.setEmailAddress(user.getEmailAddress());
		userDto.setActive(user.isActive());
		userDto.setCreatedBy(user.getCreatedBy());
		userDto.setCreatedDate(user.getCreatedDate());
		userDto.setUpdatedBy(user.getUpdatedBy());
		userDto.setUpdatedDate(user.getUpdatedDate());
		return userDto;
	}
}
