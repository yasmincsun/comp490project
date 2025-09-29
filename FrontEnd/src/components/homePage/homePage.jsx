import './homePage.css'
import React, { useState } from 'react';

function MyForm() {
    const [inputValue, setInputValue] = useState('');
    const [error, setError] = useState('');

    const validateInput = (value) => {
        if (value.trim() === '') {
            return 'Input cannot be empty.';
        }
        if (!/^\d+$/.test(value)) { // Example: only numbers allowed
            return 'Input must be a number.';
        }
        return ''; // No error
    };

    const handleChange = (event) => {
        const newValue = event.target.value;
        setInputValue(newValue);
        setError(validateInput(newValue)); // Validate on change
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const finalError = validateInput(inputValue);
        if (finalError) {
            setError(finalError);
            // Prevent form submission
        } else {
            // Form is valid, proceed with submission
            console.log('Form submitted with value:', inputValue);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <input type="text" value={inputValue} onChange={handleChange} />
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <button type="submit">Submit</button>
        </form>
    );
}

export default MyForm;
