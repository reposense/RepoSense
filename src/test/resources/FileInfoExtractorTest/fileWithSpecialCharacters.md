# ZhangYijiong
###### \java\seedu\address\commons\events\ui\LoadPageChangedEvent.java
``` java
package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;

/**
 * Represents a
 */
public class LoadPageChangedEvent extends BaseEvent {

    private final String url;

    public LoadPageChangedEvent(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public String getUrl() {
        return url;
    }
}

```
###### \java\seedu\address\commons\events\ui\OrderPanelSelectionChangedEvent.java
``` java
package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;
import seedu.address.ui.OrderCard;

/**
 * Gets an event the panel change selection
 */
public class OrderPanelSelectionChangedEvent extends BaseEvent {

    private final OrderCard newSelection;

    public OrderPanelSelectionChangedEvent(OrderCard newSelection) {
        this.newSelection = newSelection;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public OrderCard getNewSelection() {
        return newSelection;
    }
}
```
###### \java\seedu\address\commons\events\ui\PersonPanelPathChangedEvent.java
``` java
package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;
import seedu.address.ui.PersonCard;

/**
 * Represents a change in the browser Panel
 */
public class PersonPanelPathChangedEvent extends BaseEvent {


    private final PersonCard newSelection;

    public PersonPanelPathChangedEvent(PersonCard newSelection) {
        this.newSelection = newSelection;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public PersonCard getNewSelection() {
        return newSelection;
    }
}
```
###### \java\seedu\address\logic\commands\CompleteMoreOrderCommand.java
``` java
package seedu.address.logic.commands;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;

/**
 * Implementation follows {@code CompleteOneOrderCommand}
 * Deletes n orders at the front of the queue, n is the user input.
 */
public class CompleteMoreOrderCommand extends CompleteOneOrderCommand {

    public static final String COMMAND_WORD = "completeMore";
    public static final String COMMAND_ALIAS = "cM";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Complete n orders in the current queue, n be the user input.\n"
            + "Parameters: Number (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 2";

    public static final String MESSAGE_COMPLETE_TASK_SUCCESS = "Order(s) Completed";

    private final Index targetIndex;
    private final Index numberOfTimes;

    public CompleteMoreOrderCommand(Index targetIndex, Index numberOfTimes) {
        this.targetIndex = targetIndex;
        this.numberOfTimes = numberOfTimes;
    }

    @Override
    public CommandResult execute() throws CommandException {
        int number = numberOfTimes.getOneBased();
        while (number-- != 0) {

            List<Task> taskList = model.getFilteredTaskList();

            checkIsMoreThanFullCapacity(number, taskList);

            Task taskToDelete = taskList.get(targetIndex.getZeroBased());

            deleteSelectedTask(taskToDelete);

            List<Person> personList = model.getFilteredPersonList();

            int editIndex = CommandHelper.findIndexOfMatchingPerson(taskToDelete, personList);

            updateDeletedPersonTag(personList, editIndex);
        }
        return new CommandResult(String.format(MESSAGE_COMPLETE_TASK_SUCCESS));
    }

    /**
     * Checks whether given number is more than the orders in the processing queue
     * @param number number of orders to be completed
     * @param taskList list of task
     * @throws CommandException throws exception
     */
    private void checkIsMoreThanFullCapacity(int number, List<Task> taskList) throws CommandException {
        if (number >= taskList.size()) {
            throw new CommandException("There are only " + taskList.size()
                    + " orders being cooking");
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CompleteMoreOrderCommand // instanceof handles nulls
                && this.numberOfTimes.equals(((CompleteMoreOrderCommand) other).numberOfTimes)); // state check
    }
}

```
###### \java\seedu\address\logic\commands\CompleteOneOrderCommand.java
``` java
package seedu.address.logic.commands;

import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.task.Task;
import seedu.address.model.task.exceptions.TaskNotFoundException;

/**
 * Implementation follows {@code DeleteCommand}
 * Deletes an order in the processing queue identified by its index
 */
public class CompleteOneOrderCommand extends Command {

    public static final String COMMAND_WORD = "completeOne";
    public static final String COMMAND_ALIAS = "cOne";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Completes the order identified by the index number in the processing queue.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 3";

    public static final String MESSAGE_DELETE_TASK_SUCCESS = "Completed Order: %1$s";

    protected Index targetIndex;

    public CompleteOneOrderCommand() {}

    public CompleteOneOrderCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }


    @Override
    public CommandResult execute() throws CommandException {

        List<Task> lastShownList = model.getFilteredTaskList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        Task taskToDelete = lastShownList.get(targetIndex.getZeroBased());

        deleteSelectedTask(taskToDelete);

        List<Person> personList = model.getFilteredPersonList();

        int editIndex = CommandHelper.findIndexOfMatchingPerson(taskToDelete, personList);

        updateDeletedPersonTag(personList, editIndex);

        return new CommandResult(String.format(MESSAGE_DELETE_TASK_SUCCESS, taskToDelete));
    }

    /**
     * Deletes the selected task from taskList
     * @param taskToDelete task to be deleted
     */
    protected void deleteSelectedTask(Task taskToDelete) {
        try {
            model.deleteTask(taskToDelete);
        } catch (TaskNotFoundException enfe) {
            assert false : "The target task cannot be missing";
        }
    }

    /**
     * Marks matching person in the personList as "Cooked"
     * @param personList list of person
     * @param editIndex index of the person in the personList to be edited
     * @throws CommandException throws various exceptions
     */
    protected void updateDeletedPersonTag(List<Person> personList, int editIndex) throws CommandException {
        try {
            Person personToEdit = personList.get(editIndex);
            // labels order with tag "Cooked"
            Person editedPerson = CommandHelper.createNewTaggedPerson(personToEdit, "Cooked");

            model.updatePerson(personToEdit, editedPerson);
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        } catch (NullPointerException npe) {
            throw new CommandException("No matching order in order queue");
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(ProcessOrderCommand.MESSAGE_DUPLICATE_TASK);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CompleteOneOrderCommand // instanceof handles nulls
                && this.targetIndex.equals(((CompleteOneOrderCommand) other).targetIndex)); // state check
    }
}
```
###### \java\seedu\address\logic\commands\LoadCommand.java
``` java
package seedu.address.logic.commands;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.LoadPageChangedEvent;
import seedu.address.logic.commands.exceptions.CommandException;

/**
 * Selects a person identified using it's last displayed index from the address book
 * and show the path to the address of the person identified
 */
public class LoadCommand extends Command {

    public static final String COMMAND_WORD = "load";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Load the inputted web page "
            + "Parameters: web page link\n"
            + "Example: " + COMMAND_WORD + " https://www.google.com.sg "
            + "WARNING: PLEASE ENSURE URL ENTERED IS VALID AS SYSTEM WOULD NOT "
            + " FEEDBACK ERROR MESSAGE WHEN PAGE IS INVALID";

    public static final String MESSAGE_LOAD_PAGE_SUCCESS = "Load Page: %1$s, need to have https://\n"
            + "It could take a while if internet connection is slow.\n"
            + "However, if page does not load after a long time, please double check url entered";

    private final String url;

    public LoadCommand(String url) {
        this.url = url;
    }

    @Override
    public CommandResult execute() throws CommandException {

        EventsCenter.getInstance().post(new LoadPageChangedEvent(url));
        return new CommandResult(String.format(MESSAGE_LOAD_PAGE_SUCCESS, url));

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof LoadCommand // instanceof handles nulls
                && this.url.equals(((LoadCommand) other).url)); // state check
    }
}


```
###### \java\seedu\address\logic\commands\PathCommand.java
``` java
package seedu.address.logic.commands;

import java.util.List;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.ui.PersonPanelPathChangedEvent;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.ui.PersonCard;

/**
 * Selects a person identified using it's last displayed index from the address book
 * and show the path to the address of the person identified
 */
public class PathCommand extends Command {

    public static final String COMMAND_WORD = "path";
    public static final String COMMAND_ALIAS = "p";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Show the path to the address of the person identified "
            + "by the index number used in the last person listing\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 3";

    public static final String MESSAGE_PATH_PERSON_SUCCESS = "Path to Person: %1$s";

    private final Index targetIndex;

    public PathCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute() throws CommandException {

        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        PersonCard personToFindPath = new PersonCard(
                lastShownList.get(targetIndex.getZeroBased()), targetIndex.getOneBased());
        EventsCenter.getInstance().post(new PersonPanelPathChangedEvent(personToFindPath));
        return new CommandResult(String.format(MESSAGE_PATH_PERSON_SUCCESS, targetIndex.getOneBased()));

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PathCommand // instanceof handles nulls
                && this.targetIndex.equals(((PathCommand) other).targetIndex)); // state check
    }
}

```
###### \java\seedu\address\logic\commands\ProcessMoreCommand.java
``` java
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;

/**
 * Add the first multiple unprocessed order in the order queue to the application's
 * processing queue, label the corresponding orders in the
 * order queue as Processed
 */

public class ProcessMoreCommand extends ProcessNextCommand {
    public static final String COMMAND_WORD = "processMore";
    public static final String COMMAND_ALIAS = "pM";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds first n unprocessed order into the processing queue.\n"
            + "Parameters: Number (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 3";

    private int noOfTimes;

    /**
     *
     * @param noOfTimes number that processNext needed to perform
     */
    public ProcessMoreCommand(int noOfTimes) {
        this.noOfTimes = noOfTimes;
    }

    @Override
    public CommandResult execute() throws CommandException {
        requireNonNull(model);

        while (noOfTimes-- > 0) {
            super.execute();
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ProcessMoreCommand // instanceof handles nulls
                && noOfTimes == ((ProcessMoreCommand) other).noOfTimes);
    }
}

```
###### \java\seedu\address\logic\commands\ProcessNextCommand.java
``` java
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.queue.TaskList;
import seedu.address.model.task.Task;

/**
 * Add the first unprocessed order in the order queue to the application's
 * processing queue, label the corresponding order in the
 * order queue as Processed
 */

public class ProcessNextCommand extends ProcessOrderCommand {
    public static final String COMMAND_WORD = "processNext";
    public static final String COMMAND_ALIAS = "pN";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds the first unprocessed order into the processing queue\n"
            + "No parameter needed\n"
            + "Example: " + COMMAND_WORD;

    private static final String MESSAGE_All_PROCESSING = "All Order have been processed.";
    private static int noOrderToBeProcessed = -1;

    protected int targetIndex;

    protected Task toAdd;

    public ProcessNextCommand() {}

    @Override
    public CommandResult execute() throws CommandException {
        requireNonNull(model);

        targetIndex = noOrderToBeProcessed;

        List<Person> lastShownList = model.getFilteredPersonList();

        targetIndex = CommandHelper.findIndexOfPersonToBeProcessed(lastShownList);

        if (targetIndex == noOrderToBeProcessed) {
            throw new CommandException(MESSAGE_All_PROCESSING);
        }

        // inception time of the order will be shown in description
        String orderTime = getCurrentTime();

        List<Task> taskList = model.getFilteredTaskList();

        if (targetIndex >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        if (taskList.size() >= TaskList.getMaxCapacity()) {
            throw new CommandException(MESSAGE_FULL_CAPACITY);
        }

        Person personToAdd = lastShownList.get(targetIndex);

        toAdd = new Task(personToAdd, orderTime);

        if (CommandHelper.checkIsProcessed(personToAdd)) {
            throw new CommandException(MESSAGE_ALREADY_PROCESSED);
        }

        //
        Person personToEdit = personToAdd;
        // labels person with tag "Processing"
        Person editedPerson = CommandHelper.createNewTaggedPerson(personToEdit, "Processed");

        addAndTag(toAdd, personToEdit, editedPerson);

        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ProcessNextCommand // instanceof handles nulls
                && toAdd.equals(((ProcessNextCommand) other).toAdd));
    }
}

```
###### \java\seedu\address\logic\commands\ProcessOrderCommand.java
``` java
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.queue.TaskList;
import seedu.address.model.task.Task;
import seedu.address.model.task.exceptions.DuplicateTaskException;

/**
 * Add an order to the application's processing queue, label the corresponding order in the
 * order queue as Processed
 */

public class ProcessOrderCommand extends Command {
    public static final String COMMAND_WORD = "process";
    public static final String COMMAND_ALIAS = "ps";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds the order identified by the index number into the processing queue\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "New Order added: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This order already exists in the processing queue.";
    public static final String MESSAGE_FULL_CAPACITY = "Kitchen is at full capacity. No available chef.";
    public static final String MESSAGE_ALREADY_PROCESSED = "This order has already being processed.\n"
            + "WARNING: DO NOT OVERWRTIE EXISTING â€˜Processedâ€?, â€˜Cookedâ€? TAGS.";

    protected Index targetIndex;

    protected Task toAdd;

    public ProcessOrderCommand() {}

    /**
     *
     * @param targetIndex index of order to be processed in the order queue
     */
    public ProcessOrderCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute() throws CommandException {
        requireNonNull(model);

        // inception time of the order will be shown in description
        String orderTime = getCurrentTime();

        List<Person> lastShownList = model.getFilteredPersonList();
        List<Task> taskList = model.getFilteredTaskList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        if (taskList.size() >= TaskList.getMaxCapacity()) {
            throw new CommandException(MESSAGE_FULL_CAPACITY);
        }

        Person personToAdd = lastShownList.get(targetIndex.getZeroBased());

        if (CommandHelper.checkIsProcessed(personToAdd)) {
            throw new CommandException(MESSAGE_ALREADY_PROCESSED);
        }

        toAdd = new Task(personToAdd, orderTime);

        Person personToEdit = personToAdd;
        // labels person with tag "Processing"
        Person editedPerson = CommandHelper.createNewTaggedPerson(personToEdit, "Processed");

        addAndTag(toAdd, personToEdit, editedPerson);

        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    /**
     * adds the order to the processing queue, and tag the matching person
     * with tag "Processed"
     * @param toAdd task to be added
     * @param personToEdit original person
     * @param editedPerson replacement
     * @throws CommandException exceptions
     */
    protected void addAndTag(Task toAdd, Person personToEdit, Person editedPerson) throws CommandException {
        try {
            model.addTask(toAdd);

            try {
                model.updatePerson(personToEdit, editedPerson);
            } catch (DuplicatePersonException dpe) {
                throw new CommandException(MESSAGE_DUPLICATE_TASK);
            } catch (PersonNotFoundException pnfe) {
                throw new AssertionError("The target person cannot be missing");
            }
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);


        } catch (DuplicateTaskException e) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK);
        }
    }

    protected String getCurrentTime() {
        Date date = new Date();
        date.getTime();

        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY) + ":"
                + cal.get(Calendar.MINUTE) + ":"
                + cal.get(Calendar.SECOND);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ProcessOrderCommand // instanceof handles nulls
                && targetIndex.equals(((ProcessOrderCommand) other).targetIndex));
    }
}
```
###### \java\seedu\address\logic\commands\TagOrderCommand.java
``` java
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Halal;
import seedu.address.model.person.Name;
import seedu.address.model.person.Order;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Vegetarian;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

/**
 * Tag an existing order in the order queue with given word
 */

public class TagOrderCommand extends Command {
    public static final String COMMAND_WORD = "tag";
    public static final String COMMAND_ALIAS = "t";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Tags the order identified by the index number with given tag\n"
            + "Parameters: String (description)\n"
            + "Example: " + COMMAND_WORD + " Delivering";

    public static final String MESSAGE_TAGGED_ORDER_SUCCESS = "Order tagged: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This order already exists in the processing queue";
    public static final String MESSAGE_ONE_TAG_ONLY = "Please enter one tag at a time";

    private Index targetIndex;
    private String tagWord;

    /**
     * @param index of the order in the filtered order list to edit
     * @param tagWord word the user wants to tag on the order
     */
    public TagOrderCommand(Index index, String tagWord) {
        requireNonNull(index);
        requireNonNull(tagWord);

        this.targetIndex = index;
        this.tagWord = tagWord;
    }

    @Override
    public CommandResult execute() throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(targetIndex.getZeroBased());

        // labels person with tag "Processing"
        Person editedPerson = createNewTaggedPerson(personToEdit, tagWord);

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(String.format(MESSAGE_TAGGED_ORDER_SUCCESS, editedPerson));
    }



    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TagOrderCommand // instanceof handles nulls
                && tagWord.equals(((TagOrderCommand) other).tagWord))
                && targetIndex.equals(((TagOrderCommand) other).targetIndex);
    }

    /**
     * @param personToEdit
     * @param tag word to be tagged on the person
     * @return a updated person with tag attached
     */
    protected Person createNewTaggedPerson(Person personToEdit, String tag) {
        assert personToEdit != null;

        Address updatedAddress = personToEdit.getAddress();
        Halal updatedHalal = personToEdit.getHalal();
        Vegetarian updatedVegetarian = personToEdit.getVegetarian();
        Name updatedName = personToEdit.getName();
        Phone updatedPhone = personToEdit.getPhone();
        Order updatedOrder = personToEdit.getOrder();
        UniqueTagList updatedTags = new UniqueTagList(personToEdit.getTags());

        try {
            updatedTags.add(new Tag(tag));
        } catch (UniqueTagList.DuplicateTagException dte) {
            //does not add tag "processing" if already exists
        }
        return new Person(updatedName, updatedPhone, updatedOrder, updatedAddress,
                updatedHalal, updatedVegetarian, updatedTags);
    }
}

```
###### \java\seedu\address\logic\parser\CompleteMoreOrderCommandParser.java
``` java
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.CompleteMoreOrderCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new CompleteMoreOrderCommand object
 */
public class CompleteMoreOrderCommandParser implements Parser<CompleteMoreOrderCommand> {

    private static final String NUMBER_FRONT_OF_QUEUE = "1";

    /**
     * Parses the given {@code String} of arguments in the context of the CompleteMoreOrderCommand
     * and returns an CompleteMoreOrderCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public CompleteMoreOrderCommand parse(String args) throws ParseException {
        try {
            Index numberOfTimes = ParserUtil.parseIndex(args);
            Index index = ParserUtil.parseIndex(NUMBER_FRONT_OF_QUEUE);
            return new CompleteMoreOrderCommand(index, numberOfTimes);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, CompleteMoreOrderCommand.MESSAGE_USAGE));
        }
    }

}


```
###### \java\seedu\address\logic\parser\CompleteOneOrderCommandParser.java
``` java
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.CompleteOneOrderCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new CompleteOneOrderCommand object
 */
public class CompleteOneOrderCommandParser implements Parser<CompleteOneOrderCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the CompleteOneOrderCommand
     * and returns an CompleteOneOrderCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public CompleteOneOrderCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new CompleteOneOrderCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, CompleteOneOrderCommand.MESSAGE_USAGE));
        }
    }

}

```
###### \java\seedu\address\logic\parser\LoadCommandParser.java
``` java
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.LoadCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ProcessOrderCommand object
 */
public class LoadCommandParser implements Parser<LoadCommand> {

    /**
     * Parses the given {@code String} of arguments in the context
     * of the ProcessOrderCommand and returns an ProcessOrderCommand object for execution.
     */
    public LoadCommand parse(String args) throws ParseException {
        if (args.trim().equals("")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, LoadCommand.MESSAGE_USAGE));
        }

        return new LoadCommand(args.trim());
    }
}


```
###### \java\seedu\address\logic\parser\PathCommandParser.java
``` java
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.PathCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new SelectCommand object
 */
public class PathCommandParser implements Parser<PathCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the PathCommand
     * and returns an PathCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public PathCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new PathCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, PathCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\logic\parser\ProcessMoreCommandParser.java
``` java
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ProcessMoreCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ProcessMoreCommand object
 */
public class ProcessMoreCommandParser implements Parser<ProcessMoreCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the CompleteMoreOrderCommand
     * and returns an CompleteMoreOrderCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ProcessMoreCommand parse(String args) throws ParseException {
        try {
            int numberOfTimes = Integer.parseInt(args.trim());
            if (numberOfTimes <= 0) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProcessMoreCommand.MESSAGE_USAGE));
            }
            return new ProcessMoreCommand(numberOfTimes);
        } catch (NumberFormatException nfe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, "Number must be numerical"));
        }
    }
}



```
###### \java\seedu\address\logic\parser\ProcessNextCommandParser.java
``` java
package seedu.address.logic.parser;

import seedu.address.logic.commands.ProcessNextCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ProcessOrderCommand object
 */
public class ProcessNextCommandParser implements Parser<ProcessNextCommand> {

    /**
     * Parses the given {@code String} (String is none in this case)of arguments in the context
     * of the ProcessOrderCommand and returns an ProcessOrderCommand object for execution.
     */
    public ProcessNextCommand parse(String args) throws ParseException {
        return new ProcessNextCommand();
    }
}

```
###### \java\seedu\address\logic\parser\ProcessOrderCommandParser.java
``` java
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.ProcessOrderCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ProcessOrderCommand object
 */
public class ProcessOrderCommandParser implements Parser<ProcessOrderCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ProcessOrderCommand
     * and returns an ProcessOrderCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ProcessOrderCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new ProcessOrderCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProcessOrderCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\model\task\Count.java
``` java
package seedu.address.model.task;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Implementation follows {@code Price}
 * Represents an Task's past order count number
 * Guarantees: immutable; is valid as declared in {@link #isValidCount(String)}
 */
public class Count {


    public static final String MESSAGE_COUNT_CONSTRAINTS =
            "Count numbers can only be positive integers";
    public static final String COUNT_VALIDATION_REGEX = "\\d{1,}";
    public final String value;

    /**
     * Constructs a {@code Count}.
     *
     * @param count A valid count number.
     */
    public Count(String count) {
        requireNonNull(count);
        checkArgument(isValidCount(count), MESSAGE_COUNT_CONSTRAINTS);
        this.value = count;
    }

    /**
     * Returns true if a given string is a valid task count number.
     */
    public static boolean isValidCount(String test) {
        return test.matches(COUNT_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Count // instanceof handles nulls
                && this.value.equals(((Count) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     *  Returns count in integer form to be able used by {@code compareTo} in Task
     */
    public int toInt() {
        return Integer.parseInt(value);
    }
}
```
###### \java\seedu\address\model\task\Distance.java
``` java
package seedu.address.model.task;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Implementation follows {@code Count}
 * Represents an Task's distance
 * Guarantees: immutable; is valid as declared in {@link #isValidDistance(String)}
 */
public class Distance {


    public static final String MESSAGE_DISTANCE_CONSTRAINTS =
            "Distance numbers can only be positive integers, in terms of km";
    public static final String DISTANCE_VALIDATION_REGEX = "\\d{1,}";
    public final String value;

    /**
     * Constructs a {@code Distance}.
     *
     * @param distance A valid distance number.
     */
    public Distance(String distance) {
        requireNonNull(distance);
        checkArgument(isValidDistance(distance), MESSAGE_DISTANCE_CONSTRAINTS);
        this.value = distance;
    }

    /**
     * Returns true if a given string is a valid task distance number.
     */
    public static boolean isValidDistance(String test) {
        return test.matches(DISTANCE_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Distance // instanceof handles nulls
                && this.value.equals(((Distance) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     *  Returns distance in integer form to be able used by {@code compareTo} in Task
     */
    public int toInt() {
        return Integer.parseInt(value);
    }
}

```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    private void loadGoogleMapAddressPage(Person person) {
        loadPage(GOOGLE_MAP_SEARCH_PAGE + person.getAddress().getGoogleMapSearchForm());
    }
```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    private void loadGoogleMapPathPage(Person person) {
        loadPage(GOOGLE_MAP_PATH_SEARCH_PAGE + Address.ADDRESS_USER_OWN
                + "/" + person.getAddress().getGoogleMapSearchForm());
    }

```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    private void loadUserInputPage(String url) {
        loadPage(url);
    }

    public void loadPage(String url) {
        Platform.runLater(() -> browser.getEngine().load(url));
    }

    /**
     * Loads a default HTML file with a background that matches the general theme.
     */
    private void loadDefaultPage() {
        URL defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE);
        loadPage(defaultPage.toExternalForm());
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        browser = null;
    }

```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadGoogleMapAddressPage(event.getNewSelection().person);
    }

```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    @Subscribe
    private void handlePersonPanelPathChangedEvent(PersonPanelPathChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadGoogleMapPathPage(event.getNewSelection().person);
    }

```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    @Subscribe
    private void handleLoadPageChangedEvent(LoadPageChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadUserInputPage(event.getUrl());
    }
}

```
###### \java\seedu\address\ui\OrderCard.java
``` java
package seedu.address.ui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.task.Task;

/**
 * An UI component that displays information of a {@code Event}.
 */
public class OrderCard extends UiPart<Region> {
    private static final String FXML = "OrderCard.fxml";

    public final Task task;

    @FXML
    private HBox cardPane;
    @FXML
    private Label order;
    @FXML
    private Label id;
    @FXML
    private Label address;
    @FXML
    private Label description;

    public OrderCard(Task task, int displayedIndex) {
        super(FXML);
        this.task = task;
        id.setText(displayedIndex + ". ");
        bindListeners(task);
    }

    /**
     * Binds the individual UI elements to observe their respective {@code Task} properties
     * so that they will be notified of any changes.
     */
    private void bindListeners(Task task) {
        order.textProperty().bind(Bindings.convert(task.orderObjectProperty()));
        address.textProperty().bind(Bindings.convert(task.addressObjectProperty()));
        description.textProperty().bind(Bindings.convert(task.descriptionObjectProperty()));

    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof OrderCard)) {
            return false;
        }

        // state check
        OrderCard card = (OrderCard) other;
        return id.getText().equals(card.id.getText())
                && task.equals(card.task);
    }
}
```
###### \java\seedu\address\ui\OrderQueuePanel.java
``` java
package seedu.address.ui;

import java.util.logging.Logger;

import org.fxmisc.easybind.EasyBind;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.OrderPanelSelectionChangedEvent;
import seedu.address.model.task.Task;

/**
 * Implementation follows {@code PersonListPanel}
 * Panel containing the queue of orders.
 */
public class OrderQueuePanel extends UiPart<Region> {
    private static final String FXML = "OrderQueuePanel.fxml";
    private final Logger logger = LogsCenter.getLogger(OrderQueuePanel.class);

    @FXML
    private ListView<OrderCard> orderListView;

    public OrderQueuePanel(ObservableList<Task> taskList) {
        super(FXML);
        setConnections(taskList);
        registerAsAnEventHandler(this);
    }

    private void setConnections(ObservableList<Task> taskList) {
        ObservableList<OrderCard> mappedList = EasyBind.map(
                taskList, (task) -> new OrderCard(task, taskList.indexOf(task) + 1));
        orderListView.setItems(mappedList);
        orderListView.setCellFactory(listView -> new OrderQueueViewCell());
        setEventHandlerForSelectionChangeEvent();
    }

    private void setEventHandlerForSelectionChangeEvent() {
        orderListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        logger.fine("Selection in order list panel changed to : '" + newValue + "'");
                        raise(new OrderPanelSelectionChangedEvent(newValue));
                    }
                });
    }

    /**
     * Scrolls to the {@code OrderCard} at the {@code index} and selects it.
     */
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            orderListView.scrollTo(index);
            orderListView.getSelectionModel().clearAndSelect(index);
        });
    }


    /**
     * Custom {@code ListCell} that displays the graphics of a {@code OrderCard}.
     */
    class OrderQueueViewCell extends ListCell<OrderCard> {

        @Override
        protected void updateItem(OrderCard order, boolean empty) {
            super.updateItem(order, empty);

            if (empty || order == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(order.getRoot());
            }
        }
    }
}
```
