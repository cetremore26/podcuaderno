package com.podcuaderno.notebooks.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record NotebookTitleRequest(@NotBlank @Size(max = 150) String title) {}
