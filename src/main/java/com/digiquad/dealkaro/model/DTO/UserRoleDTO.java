package      com.digiquad.dealkaro.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserRoleDTO implements Serializable {
    private Integer id;
    private String name;
}
