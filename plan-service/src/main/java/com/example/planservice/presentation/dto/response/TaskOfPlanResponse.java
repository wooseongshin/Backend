package com.example.planservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.planservice.domain.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskOfPlanResponse {
    private Long id;
    private String title;
    private List<Long> labels;
    private Long tabId;
    private Long assigneeId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder
    private TaskOfPlanResponse(Long id, String title, Long tabId, List<Long> labels, Long assigneeId,
                               LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.title = title;
        this.labels = labels;
        this.tabId = tabId;
        this.assigneeId = assigneeId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TaskOfPlanResponse toPlanResponse(Task task) {
        return builder()
            .id(task.getId())
            .title(task.getName())
            .labels(task.getLabelOfTasks() != null ?
                task.getLabelOfTasks().stream().map(labelOfTask -> labelOfTask.getLabel().getId()).toList() :
                null)
            .tabId(task.getTab().getId())
            .assigneeId(task.getWriter() != null ? task.getWriter().getId() : null)
            .startDate(task.getStartDate() != null ? task.getStartDate() : null)
            .endDate(task.getEndDate() != null ? task.getEndDate() : null)
            .build();
    }
}
