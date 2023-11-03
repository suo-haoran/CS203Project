package com.cs203g3.ticketing.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Attachment {
    private String fileName;
    private String filePath;
}
