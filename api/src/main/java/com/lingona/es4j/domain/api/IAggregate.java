package com.lingona.es4j.domain.api;

import java.util.Collection;
import java.util.UUID;


public interface IAggregate {

    UUID getId();      // { get; }
    int  getVersion(); // { get; }

    void applyEvent(Object event);

    Collection   getUncommittedEvents();
    void       clearUncommittedEvents();

    IMemento getSnapshot();

//  void throwHandlerNotFound(Object eventMessage);
}

/*
namespace CommonDomain
{
	using System;
	using System.Collections;

	public interface IAggregate
	{
		Guid Id { get; }
		int Version { get; }

		void ApplyEvent(object @event);
		ICollection GetUncommittedEvents();
		void ClearUncommittedEvents();

		IMemento GetSnapshot();
	}
}
*/
