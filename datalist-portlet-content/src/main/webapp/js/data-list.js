/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
//Create the dl_v1 variable if it does not already exist
var dl_v1 = dl_v1 || {};

/*
 * Switch jQuery to extreme noConflict mode, keeping a reference to it in the dl_v1
 * variable if one doesn't already exist
 */
if (!dl_v1.jQuery) {
    dl_v1.jQuery = jQuery.noConflict(true);
}
else {
    jQuery.noConflict(true);
}

/*
 * Move Fluid to a local reference
 */
if (!dl_v1.fluid) {
    dl_v1.fluid = fluid;
}
fluid = null;
fluid_1_2 = null;

//For convenience
Date.prototype.dlFormat = function (mask, utc) {
 return dateFormat(this, mask, utc);
};


(function($, fluid, dl) {
    "use strict";
    
    if (dl.commonInit) {
        return;
    }
    
    $(function() {
        $('a.todo').unbind('click').click(function() {
            alert('Link Not Implemented');
            return false;
        });
    });
    
    /*
     * Clickable cell/row utilities
     */
    dl.util = dl.util || {};
    dl.util.clickableContainer = function(container, selector) {
        if (!selector) {
            selector = ".dl-clickable";
        }
        
        var containerObj = $(container);
        var selectedObjs = containerObj.find(selector);
        selectedObjs.live("click", function(event) { 
            //Simulate a click on the first anchor and click on it
            var links = $(this).find("a");
            var firstLink = links.first();
            var url = firstLink.attr('href');
            var target = firstLink.attr('target') || "_self";
            window.open(url, target);
            return false;
        });
    };
    dl.util.templateUrl = function(url) {
    	return url.replace(/_TMPLT/g, "}").replace(/TMPLT_/g, "${");
    };
    
    

    /*
     * Define some data utilities to make it easier to track inline generated json
     */
    dl.data = dl.data || {};
    dl.data.put = function(ns, key, data) {
        dl.data[ns] = dl.data[ns] || {};
        dl.data[ns][key] = data;
    };
    dl.data.get = function(ns, key) {
        dl.data[ns] = dl.data[ns] || {};
        return dl.data[ns][key];
    };
    dl.data.contains = function(ns, key) {
        return ns in dl.data && key in dl.data[ns];
    };


    /*
     * Pager utilities
     */
    dl.pager = dl.pager || {};
    dl.pager.consistentGappedPageStrategy = function (endLinkCount, midLinkCount) {
        if (!endLinkCount) {
            endLinkCount = 1;
        }
        if (!midLinkCount) {
            midLinkCount = endLinkCount;
        }
        var midWidth = endLinkCount + (midLinkCount * 2);
        
        
        return function (count, first, mid) {
            var pages = [];
            
            var anchoredLeft = mid < midWidth;
            var anchoredRight = mid >= count - midWidth;
            var paddedMidWidth = midWidth + 2;
            var midStart = mid - midLinkCount;
            var midEnd = mid + midLinkCount;
            
            var lastSkip = false;
            
            for (var page = 0; page < count; page++) {
                if (
                    page < endLinkCount || // start pages
                    count - page <= endLinkCount || // end pages
                    (anchoredLeft && page < paddedMidWidth) || // pages if no skipped pages between start and mid
                    (anchoredRight && page >= count - paddedMidWidth) || // pages if no skipped pages between mid and end
                    (page >= midStart && page <= midEnd) // pages around the mid
                ) {
                    pages.push(page);
                    lastSkip = false;
                }
                else if (!lastSkip) {
                    pages.push(-1);
                    lastSkip = true;
                }
            }
            
            return pages;
        };
    }; 
    
    /*
     * Sorter that will use 
     */
    dl.pager.columnTypeAwareSorter = function(overallThat, model) {
        var dataModel = overallThat.options.dataModel;
        var roots = {};
        var columnDefs = overallThat.options.columnDefs;
        var columnDef = fluid.pager.findColumnDef(columnDefs, model.sortKey);
        var sortrecs = [];
        for (var i = 0; i < model.totalRange; ++ i) {
            sortrecs[i] = {
                index: i,
                value: fluid.pager.fetchValue(overallThat, dataModel, i, columnDef.valuebinding, roots)
            };
        }
        
        if (sortrecs.length > 0) {
//            var columnType = typeof sortrecs[0].value;
            
            var sortfunc;
            if (columnDef.sortFunction) {
                sortfunc = columnDef.sortFunction(model);
            }
            else {
                sortfunc = dl.pager.defaultSorter(model, columnDef.sortValueExtractor);
            }
        
            sortrecs.sort(sortfunc);
        }
        
        return fluid.transform(sortrecs, function (row) {return row.index; });
    };
    
    /*
     * Sorter that tries to use a valueExtractor if defined, if not simply uses
     * rec.value to perfom the sorting logic
     */
    dl.pager.defaultSorter = function(model, valueExtractor) {
        function extractValue(rec) {
            return valueExtractor ? valueExtractor(model, rec) : rec.value;
        }
    
        return function (arec, brec) {
            var a = extractValue(arec);
            var b = extractValue(brec);
            return a === b ? 0 : (a > b? model.sortDir : -model.sortDir);
        };
    };
    
    /*
     * Converts the record's value into a float
     */
    dl.pager.numberExtractor = function(model, rec) {
        return parseFloat(rec.value);
    };
    
    /*
     * Converts the record's value into a float
     */
    dl.pager.currencyExtractor = function(model, rec) {
        return parseFloat(rec.value.replace(/\$|,/g,""));
    };
    
    /*
     * Converts the record's value into a date
     */
    dl.pager.dateExtractor = function (model, rec) {
        return new Date(rec.value);
    };
    
    /*
     * Converts the record's value into a date
     */
    dl.pager.mmyyyyDateExtractor = function (model, rec) {
        var parts = rec.value.split("/");
        var month = parseInt(parts[0]);
        var year = parseInt(parts[1]);
        return new Date(year, month - 1);
    };
    
    /*
     * Converts the record's value into a date
     */
    dl.pager.MMMMyyyyDateExtractor = function (model, rec) {
        var dateStr = rec.value.replace(" ", " 1 ");
        return new Date(dateStr);
    };
    
    /*
     * Help for creating a pager columnDef entry
     */
    dl.pager.colDef = function(name, opts) {
        var colDef = {
            key: name, 
            valuebinding: "*." + name
        };
        
        if (opts != undefined) {
            return $.extend(true, {}, colDef, opts);
        }
        
        return colDef;
    };
    
    /*
     * Help for creating a pager columnDef entry for a column with a link in it
     */
    dl.pager.linkColDef = function(name, link, opts) {
        var colDef = {
            key: name, 
            valuebinding: "*." + name, 
            components: { 
                target: link,
                linktext: "${*." + name + "}"
            }
        };
        
        if (opts != undefined) {
            return $.extend(true, {}, colDef, opts);
        }
        
        return colDef;
    };
    
    /**
     * Refresh a Fluid pager with a new data model.  This method will also 
     * return the pager to the first page
     * 
     * @param pager  {Object}  pager to be refreshed
     * @param data   {Object}  new data model
     */
    dl.pager.refresh = function (pager, data) {
        var newModel = fluid.copy(pager.model);
        newModel.totalRange = data.length;
        newModel.pageIndex = 0;
        newModel.pageCount = Math.max(1, Math.floor((newModel.totalRange - 1) / newModel.pageSize) + 1);
        fluid.clear(pager.options.dataModel);
        fluid.model.copyModel(pager.options.dataModel, data);
        pager.permutation = undefined;
        pager.events.onModelChange.fire(newModel, pager.model, pager);
        fluid.model.copyModel(pager.model, newModel);
        pager.events.initiatePageChange.fire({forceUpdate: true});
    };
    
    dl.pager.defaultDataExtractor = function (key, data) {
        if (key == undefined) {
            return data;
        }
        
        return data[key];
    };
    
    dl.pager.init = function(selector, options) {
        var defaults = {
            dataModel: [],
            bodyRenderer : {
                type : "fluid.pager.selfRender",
                options : {
                    selectors : {
                        root : ".dl-pager-table-data"
                    }
                }
            },
            pagerBar : {
                type : "fluid.pager.pagerBar",
                options : {
                    pageList : {
                        type : "fluid.pager.renderedPageList",
                        options : {
                            pageStrategy : dl.pager.consistentGappedPageStrategy(2, 2)
                        }
                    }
                }
            },
            model : {
                pageSize : 10
            },
            summary: {
                type: "fluid.pager.summary", 
                options: {
                  message: "%first-%last of %total"
                }
            },
            strings : {
                last : ""
            },
            sorter : dl.pager.columnTypeAwareSorter,
            dataList: {
                dataKey: undefined,
                dataExtractor: dl.pager.defaultDataExtractor,
                dataLoadErrorMsg: "Error Loading Data",
                dataLoadCallback: undefined,
                pagerContainerClass: "ui-tabs-panel"
            }
        };
        
        var opts = $.extend(true, {}, defaults, options);
        
        $.log(selector + ": Initializing pager with data from: " + opts.dataList.url);
        
        //Toggle the navbar based on the data
        var navBar = $(selector + " div.dl-pager-navbar");
        if (opts.dataModel.length > 10) {
            navBar.show();
        }
        else {
            navBar.hide();
        }
        
        var pager = fluid.pager(selector, opts);
        $.log(selector + ": Pager initialized");
        
        if (opts.dataList.url != undefined) {
            var pagerDiv = $(pager.container).children(".fl-pager");
            
            var pagerError = function() {
                $.log(selector + ": Pager Data Error, displaying error message");
                dl.pager.refresh(pager, []);
                navBar.hide();
                dl.block(pagerDiv, opts.dataList.dataLoadErrorMsg);
                pagerDiv.unmask();
                addPagerRefreshLinkHandler();
            };
            
            var pagerRefresh = function(args) {
                var args = args || {};
                
                $.log(function() { return selector + ": Loading pager data from: " + opts.dataList.url + " with args " + JSON.stringify(args.data || {}); });

                var ajaxManager = dl.ajaxQueueManager(opts.dataList.url);
                ajaxManager.add({
                    url: opts.dataList.url,
                    data: args.data || {},
                    beforeSend: function () {
                        pagerDiv.mask("Loading ...");
                    },
                    success: function(data) {
                        try {
                            $.log(function() { return selector + ": Successfully retrieved pager data, refreshing pager: " + JSON.stringify(data); });
                            
                            data = opts.dataList.dataExtractor(opts.dataList.dataKey, data);
                            
                            pagerDiv.unmask();
                            dl.unblock(pagerDiv);
                            dl.pager.refresh(pager, data);
                        
                            if (data.length > 10) {
                                navBar.show();
                            }
                            else {
                                navBar.hide();
                            }
                            
                            if (opts.dataList.dataLoadCallback != undefined) {
                            	opts.dataList.dataLoadCallback(data);
                            }
                        }
                        catch (err) {
                            $.log("Error parsing pager data: " + JSON.stringify(err));
                            pagerError();
                        }
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        pagerError();
                    }
                });
            };
            
            var addPagerRefreshLinkHandler = function() {
                $(selector + " a.dl-refresh").click(function() {
                    pagerRefresh({
                        data: {refresh: "true"}
                    });
                    return false;
                });
            };
            
            var tabElement;
            var pagerElement = $(selector);
            if (pagerElement.hasClass(opts.dataList.pagerContainerClass)) {
            	//If the pager element is the tab panel add the refresh handler there
            	tabElement = pagerElement;
            }
            else {
            	//Look for first parent that is a tab panel
            	tabElement = pagerElement.parent("." + opts.dataList.pagerContainerClass);
            }
            
            //TODO need to make this all work when not in a mini tab :(
            
            //Add the pager refresh function to the list of callbacks for the tab
            var refreshCallbacks = tabElement.data("loadPagerData");
            if (refreshCallbacks == undefined) {
            	refreshCallbacks = {};
            	tabElement.data("loadPagerData", refreshCallbacks);
            }
            refreshCallbacks[selector] = pagerRefresh;
            
            addPagerRefreshLinkHandler();
        }
    };
    
    dl.pager.show = function(pagerContainer) {
        var loadPagerData = $(pagerContainer).data("loadPagerData");
        if (loadPagerData != undefined) {
            $.log("First time container " + pagerContainer + " has been viewed, loading pager data");
            for (var selector in loadPagerData) {
                $.log("Loading pager data for: " + selector);
                loadPagerData[selector]();
            }
            
            delete $(pagerContainer).data()["loadPagerData"];
        }
    };
    
    dl.tabs = function(selector) {
        $(selector).tabs({
            show: function(event, ui) {
                $.log("Showing tab: " + ui.index);
                dl.pager.show(ui.panel);
            }
        });
    };
    
    dl.ajaxQueueManagers = dl.ajaxQueueManagers || {}; 
    dl.ajaxQueueManager = function(url) {
        if (dl.ajaxQueueManagers[url] != undefined) {
            return dl.ajaxQueueManagers[url];
        }
        
        dl.ajaxQueueManagers[url] = $.manageAjax.create(url, {
            queue: true
        });
        
        return dl.ajaxQueueManagers[url];
    };
    
    dl.unblock = function(target) {
        var target = $(target);
        var blocked = target.data("dl.block");
        if (blocked) {
            target.find(".dl-block").remove();
            target.data("dl.block", false);
        }
    };
    
    dl.block = function(target, message) {
        var target = $(target);
        dl.unblock(target);
        target.data("dl.block", true);
        if (target.css('position') == 'static') {
            target.css('position', 'relative');
        }
        target.append(
                "<div class=\"dl-block block-message-wraper\">" +
                    "<div class=\"dl-block block-message fl-widget\">" + message + "</div>" +
        		"</div>");
    };
    

    dl.maxEarningsToggleRepeats = 10;
    
    dl.scheduleToggleEarnings = function(earningsToggle, updateAmmountVisibility, repeatCount) {
        if (repeatCount == undefined) {
            repeatCount = dl.maxEarningsToggleRepeats;
        }
        
        var earningsToggleData = earningsToggle.data();
        
        if (earningsToggleData.toggleEarningsTimerId != undefined) {
            $.log("Canceling existing toggle earnings timer: " + earningsToggleData.toggleEarningsTimerId);
            clearTimeout(earningsToggleData.toggleEarningsTimerId);
            delete earningsToggleData.toggleEarningsTimerId;
        }
        
        earningsToggleData.toggleEarningsTimerId = setTimeout(function() {
            var earningsToggleData = earningsToggle.data();
            delete earningsToggleData.toggleEarningsTimerId;
            
            var updated = updateAmmountVisibility(earningsToggle);
            if (updated) {
                $.log("Timer updated ammount visibility after " + (1 + dl.maxEarningsToggleRepeats - repeatCount) + " attempts");
                return;
            }
            
            if (repeatCount > 0) {
                $.log(repeatCount + " repeats left, scheduling another earnings toggle update");
                dl.scheduleToggleEarnings(earningsToggle, updateAmmountVisibility, repeatCount - 1);
            }
            
        }, 10);
        $.log("Scheduled toggle earnings timer: " + earningsToggleData.toggleEarningsTimerId);
    };

    dl.commonInit = true;
})(dl_v1.jQuery, dl_v1.fluid, dl_v1);