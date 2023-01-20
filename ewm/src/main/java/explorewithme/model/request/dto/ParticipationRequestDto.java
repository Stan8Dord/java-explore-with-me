package explorewithme.model.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipationRequestDto {
    private long id;
    private String created;
    private long event;
    private long requester;
    private String status;
}
