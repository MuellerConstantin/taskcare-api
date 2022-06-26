package de.x1c1b.taskcare.service.presentation.rest.v1;

import de.x1c1b.taskcare.service.core.board.application.BoardService;
import de.x1c1b.taskcare.service.core.board.application.command.CreateBoardCommand;
import de.x1c1b.taskcare.service.core.board.application.command.DeleteBoardByIdCommand;
import de.x1c1b.taskcare.service.core.board.application.command.UpdateBoardByIdCommand;
import de.x1c1b.taskcare.service.core.board.application.query.FindAllBoardsWithMembershipQuery;
import de.x1c1b.taskcare.service.core.board.application.query.FindBoardByIdQuery;
import de.x1c1b.taskcare.service.core.board.domain.Board;
import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.BoardDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.CreateBoardDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.PageDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.UpdateBoardDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.mapper.BoardDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@Validated
public class BoardController {

    private final BoardService boardService;
    private final BoardDTOMapper boardDTOMapper;

    @Autowired
    public BoardController(BoardService boardService, BoardDTOMapper boardDTOMapper) {
        this.boardService = boardService;
        this.boardDTOMapper = boardDTOMapper;
    }

    @GetMapping("/users/{username}/boards")
    PageDTO<BoardDTO> findAllByMembership(@PathVariable("username") String username,
                                          @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                          @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        FindAllBoardsWithMembershipQuery query = new FindAllBoardsWithMembershipQuery(page, perPage, username);
        Page<Board> result = boardService.query(query);
        return boardDTOMapper.mapToDTO(result);
    }

    @PostMapping("/boards")
    @ResponseStatus(HttpStatus.CREATED)
    void create(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateBoardDTO dto) {
        CreateBoardCommand command = boardDTOMapper.mapToCommand(dto, userDetails.getUsername());
        boardService.execute(command);
    }

    @PatchMapping("/boards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateById(@PathVariable("id") UUID id, @RequestBody UpdateBoardDTO dto) {
        UpdateBoardByIdCommand command = boardDTOMapper.mapToCommand(dto, id);
        boardService.execute(command);
    }

    @GetMapping("/boards/{id}")
    BoardDTO findById(@PathVariable("id") UUID id) {
        FindBoardByIdQuery query = new FindBoardByIdQuery(id);
        Board result = boardService.query(query);
        return boardDTOMapper.mapToDTO(result);
    }

    @DeleteMapping("/boards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable("id") UUID id) {
        DeleteBoardByIdCommand command = new DeleteBoardByIdCommand(id);
        boardService.execute(command);
    }
}
