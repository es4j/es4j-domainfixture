package com.lingona.cd4j.persistence.api;

import java.util.Map;

/**
 *
 * @author Esfand
 */
public abstract class HeaderUpdater {
    public abstract void updateHeader(Map<String, Object> map);
}
