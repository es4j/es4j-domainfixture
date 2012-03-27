package com.lingona.cd4j.api;

import java.util.UUID;
import java.util.concurrent.Callable;


public interface IMemento extends Callable {

    UUID getId(); // { get; set; }
    void setId(UUID id);

    int  getVersion(); // { get; set; }
    void setVersion(int version);
}