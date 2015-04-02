/**
 * Angular-Validation Directive (ghiscoding)
 * https://github.com/ghiscoding/angular-validation
 *
 * @author: Ghislain B.
 * @started: 2014-02-04
 *
 * @desc: If a field becomes invalid, the text inside the error <span> or <div> will show up because the error string gets filled
 * Though when the field becomes valid then the error message becomes an empty string,
 * it will be transparent to the user even though the <span> still exist but becomes invisible since the text is empty.
 *
 */
 angular
  .module('ghiscoding.validation', ['pascalprecht.translate'])
  .directive('validation', ['$timeout', 'validationCommon', 'validationRules', function($timeout, validationCommon, validationRules) {
    return {
      restrict: "A",
      require: "ngModel",
      link: function(scope, elm, attrs, ctrl) {
        // create an object of the common c
        var commonObj = new validationCommon(scope, elm, attrs, ctrl);
        var timer;

        // construct the functions, it's just to make the code cleaner and put the functions at bottom
        var construct = {
          attemptToValidate: attemptToValidate,
          cancelValidation : cancelValidation
        }

        // attach the attemptToValidate function to the element
        ctrl.$parsers.unshift(attemptToValidate);
        ctrl.$formatters.unshift(attemptToValidate);

        // for the case of field that might be ng-disabled, we should skip validation
        // Observe the angular disabled attribute
        attrs.$observe("disabled", function(disabled) {
          if(disabled) {
            // Turn off validation when disabled
            ctrl.$setValidity('validation', true);
          } else {
            // Re-Validate the input when enabled
            ctrl.$setValidity('validation', commonObj.validate(ctrl.$viewValue, true));
          }
        });

        //----
        // Private functions declaration
        //----------------------------------

        /** Cancel current validation test and blank any leftover error message */
        function cancelValidation() {
          $timeout.cancel(timer);
          commonObj.updateErrorMsg('');
          ctrl.$setValidity('validation', true);
          elm.unbind('blur'); // unbind onBlur event, if not it will fail when input become dirty & empty
        }

        /** Validator function to attach to the element, this will get call whenever the input field is updated
         *  and is also customizable through the (typing-limit) for which inactivity this.timer will trigger validation.
         * @param string value: value of the input field
         */
        function attemptToValidate(value) {
          // pre-validate without any events just to pre-fill our validationSummary with all field errors
          // passing false as 2nd argument for not showing any errors on screen
          commonObj.validate(value, false);

          // if field is not required and his value is empty, cancel validation and exit out
          if(!commonObj.isFieldRequired() && (value === "" || value === null || typeof value === "undefined")) {
            cancelValidation();
            return value;
          }

          // invalidate field before doing any validation
          if(commonObj.isFieldRequired() || !!value) {
            ctrl.$setValidity('validation', false);
          }

          // if field is not required and his value is empty, cancel validation and exit out
          // onBlur make validation without waiting
          elm.bind('blur', function() {
            // make the regular validation of the field value
            scope.$evalAsync(ctrl.$setValidity('validation', commonObj.validate(value, true) ));
            return value;
          });

          // select(options) will be validated on the spot
          if(elm.prop('tagName').toUpperCase() === "SELECT") {
            ctrl.$setValidity('validation', commonObj.validate(value, true));
            return value;
          }

          // onKeyDown event is the default of Angular, no need to even bind it, it will fall under here anyway
          // in case the field is already pre-filled, we need to validate it without looking at the event binding
          if(typeof value !== "undefined") {
            // Make the validation only after the user has stopped activity on a field
            // everytime a new character is typed, it will cancel/restart the timer & we'll erase any error mmsg
            commonObj.updateErrorMsg('');
            $timeout.cancel(timer);
            timer = $timeout(function() {
              scope.$evalAsync(ctrl.$setValidity('validation', commonObj.validate(value, true) ));
            }, commonObj.typingLimit);
          }

          return value;
        } // attemptToValidate()
      } // link()
    }; // return;
  }]); // directive